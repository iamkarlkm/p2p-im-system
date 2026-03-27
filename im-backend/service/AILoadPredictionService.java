package com.im.backend.service;

import com.im.backend.model.kafka.PartitionMetrics;
import com.im.backend.model.kafka.TopicMetrics;
import com.im.backend.model.kafka.LoadForecast;
import com.im.backend.repository.PartitionMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * AI负载预测服务
 * 使用时间序列分析和机器学习算法预测Kafka分区未来负载
 * 支持多种预测模型：移动平均、指数平滑、线性回归、趋势分解
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class AILoadPredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(AILoadPredictionService.class);
    
    // 预测配置
    private static final int PREDICTION_HORIZON_MINUTES = 30; // 预测未来30分钟
    private static final int MIN_HISTORY_SIZE = 10; // 最小历史数据点
    private static final long METRICS_COLLECTION_INTERVAL_MS = 60000; // 1分钟收集间隔
    private static final int MAX_HISTORY_POINTS = 1440; // 保留24小时历史数据
    
    @Autowired
    private PartitionMetricsRepository partitionMetricsRepo;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final ExecutorService predictionExecutor = Executors.newFixedThreadPool(4);
    
    // 运行时状态
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    // 历史数据存储: topic -> partition -> 时间序列数据点
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, TimeSeries>> historicalData = 
        new ConcurrentHashMap<>();
    
    // 预测结果缓存: topic -> partition -> 预测结果
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, LoadForecast>> forecastCache = 
        new ConcurrentHashMap<>();
    
    // 模型性能统计
    private final ConcurrentHashMap<String, ModelPerformanceMetrics> modelPerformance = 
        new ConcurrentHashMap<>();
    
    // 预测模型配置
    private final AtomicReference<PredictionModelConfig> modelConfig = 
        new AtomicReference<>(new PredictionModelConfig());
    
    /**
     * 启动预测服务
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("启动AI负载预测服务...");
            
            // 数据收集任务
            scheduler.scheduleAtFixedRate(
                this::collectMetrics,
                0,
                METRICS_COLLECTION_INTERVAL_MS,
                TimeUnit.MILLISECONDS
            );
            
            // 预测生成任务
            scheduler.scheduleAtFixedRate(
                this::generateForecasts,
                30000,
                300000, // 5分钟生成一次预测
                TimeUnit.MILLISECONDS
            );
            
            // 模型性能评估任务
            scheduler.scheduleAtFixedRate(
                this::evaluateModelPerformance,
                60000,
                600000, // 10分钟评估一次
                TimeUnit.MILLISECONDS
            );
            
            logger.info("AI负载预测服务已启动");
        }
    }
    
    /**
     * 停止预测服务
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("停止AI负载预测服务...");
            scheduler.shutdown();
            predictionExecutor.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
                predictionExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("AI负载预测服务已停止");
        }
    }
    
    /**
     * 收集指标数据
     */
    private void collectMetrics() {
        try {
            List<PartitionMetrics> allMetrics = partitionMetricsRepo.findAll();
            
            for (PartitionMetrics metrics : allMetrics) {
                String topic = metrics.getTopicName();
                int partition = metrics.getPartitionId();
                
                // 确保存储结构存在
                historicalData.computeIfAbsent(topic, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(partition, k -> new TimeSeries(MAX_HISTORY_POINTS));
                
                // 添加数据点
                DataPoint point = new DataPoint(
                    System.currentTimeMillis(),
                    metrics.getMessageLag(),
                    metrics.getMessageRate(),
                    metrics.getCpuUsage(),
                    metrics.getMemoryUsage()
                );
                
                historicalData.get(topic).get(partition).add(point);
            }
            
        } catch (Exception e) {
            logger.error("指标收集失败", e);
        }
    }
    
    /**
     * 生成预测
     */
    private void generateForecasts() {
        if (!isRunning.get()) return;
        
        try {
            long startTime = System.currentTimeMillis();
            logger.debug("开始生成负载预测...");
            
            List<Future<?>> futures = new ArrayList<>();
            
            for (Map.Entry<String, ConcurrentHashMap<Integer, TimeSeries>> topicEntry : historicalData.entrySet()) {
                String topic = topicEntry.getKey();
                
                for (Map.Entry<Integer, TimeSeries> partitionEntry : topicEntry.getValue().entrySet()) {
                    int partition = partitionEntry.getKey();
                    TimeSeries series = partitionEntry.getValue();
                    
                    // 异步生成预测
                    Future<?> future = predictionExecutor.submit(() -> {
                        try {
                            generatePartitionForecast(topic, partition, series);
                        } catch (Exception e) {
                            logger.error("生成预测失败: Topic={}, Partition={}", topic, partition, e);
                        }
                    });
                    
                    futures.add(future);
                }
            }
            
            // 等待所有预测完成
            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS);
                } catch (Exception e) {
                    logger.error("预测任务执行失败", e);
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("负载预测完成，耗时: {}ms", duration);
            
        } catch (Exception e) {
            logger.error("生成预测失败", e);
        }
    }
    
    /**
     * 为单个分区生成预测
     */
    private void generatePartitionForecast(String topic, int partition, TimeSeries series) {
        List<DataPoint> data = series.getData();
        
        if (data.size() < MIN_HISTORY_SIZE) {
            logger.debug("历史数据不足，跳过预测: Topic={}, Partition={}, 数据点={}",
                topic, partition, data.size());
            return;
        }
        
        // 提取消息延迟序列
        List<Double> lagValues = data.stream()
            .map(DataPoint::getMessageLag)
            .map(Long::doubleValue)
            .collect(Collectors.toList());
        
        // 提取消息速率序列
        List<Double> rateValues = data.stream()
            .map(DataPoint::getMessageRate)
            .map(Long::doubleValue)
            .collect(Collectors.toList());
        
        // 使用集成模型生成预测
        LoadForecast forecast = generateEnsembleForecast(
            topic, partition, lagValues, rateValues, data
        );
        
        // 存储预测结果
        forecastCache.computeIfAbsent(topic, k -> new ConcurrentHashMap<>())
            .put(partition, forecast);
    }
    
    /**
     * 使用集成模型生成预测
     */
    private LoadForecast generateEnsembleForecast(String topic, int partition,
                                                   List<Double> lagValues,
                                                   List<Double> rateValues,
                                                   List<DataPoint> fullData) {
        
        PredictionModelConfig config = modelConfig.get();
        
        // 各模型的预测结果
        List<ModelPrediction> predictions = new ArrayList<>();
        
        // 1. 简单移动平均
        if (config.isEnableMovingAverage()) {
            predictions.add(movingAverageForecast(lagValues, config.getMovingAverageWindow()));
        }
        
        // 2. 指数平滑
        if (config.isEnableExponentialSmoothing()) {
            predictions.add(exponentialSmoothingForecast(lagValues, config.getSmoothingAlpha()));
        }
        
        // 3. 线性回归
        if (config.isEnableLinearRegression()) {
            predictions.add(linearRegressionForecast(lagValues));
        }
        
        // 4. Holt-Winters（带趋势）
        if (config.isEnableHoltWinters()) {
            predictions.add(holtWintersForecast(lagValues, 
                config.getHwAlpha(), config.getHwBeta()));
        }
        
        // 集成预测结果
        return ensemblePredictions(topic, partition, predictions, lagValues, rateValues);
    }
    
    /**
     * 移动平均预测
     */
    private ModelPrediction movingAverageForecast(List<Double> values, int window) {
        if (values.size() < window) {
            window = values.size();
        }
        
        // 计算最后window个点的平均值
        double sum = 0;
        for (int i = values.size() - window; i < values.size(); i++) {
            sum += values.get(i);
        }
        double avg = sum / window;
        
        // 计算趋势
        double trend = calculateTrend(values, window);
        
        // 生成未来30分钟的预测
        List<Double> forecast = new ArrayList<>();
        double current = avg;
        for (int i = 0; i < PREDICTION_HORIZON_MINUTES; i++) {
            current += trend;
            forecast.add(Math.max(0, current));
        }
        
        double confidence = Math.min(0.7, 0.3 + (values.size() / 1000.0));
        
        return new ModelPrediction("MovingAverage", forecast, confidence, 0.25);
    }
    
    /**
     * 指数平滑预测
     */
    private ModelPrediction exponentialSmoothingForecast(List<Double> values, double alpha) {
        if (values.isEmpty()) {
            return new ModelPrediction("ExponentialSmoothing", 
                Collections.nCopies(PREDICTION_HORIZON_MINUTES, 0.0), 0.3, 0.25);
        }
        
        // 初始平滑值
        double smoothed = values.get(0);
        
        // 应用指数平滑
        for (int i = 1; i < values.size(); i++) {
            smoothed = alpha * values.get(i) + (1 - alpha) * smoothed;
        }
        
        // 计算趋势
        double trend = calculateTrend(values, Math.min(10, values.size()));
        
        // 生成预测
        List<Double> forecast = new ArrayList<>();
        double current = smoothed;
        for (int i = 0; i < PREDICTION_HORIZON_MINUTES; i++) {
            current += trend;
            forecast.add(Math.max(0, current));
        }
        
        double confidence = Math.min(0.75, 0.35 + (values.size() / 1000.0));
        
        return new ModelPrediction("ExponentialSmoothing", forecast, confidence, 0.25);
    }
    
    /**
     * 线性回归预测
     */
    private ModelPrediction linearRegressionForecast(List<Double> values) {
        int n = values.size();
        if (n < 2) {
            return new ModelPrediction("LinearRegression",
                Collections.nCopies(PREDICTION_HORIZON_MINUTES, values.isEmpty() ? 0.0 : values.get(0)), 
                0.4, 0.3);
        }
        
        // 计算回归系数
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values.get(i);
            sumXY += i * values.get(i);
            sumX2 += i * i;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        // 计算R²
        double yMean = sumY / n;
        double ssTotal = 0, ssResidual = 0;
        for (int i = 0; i < n; i++) {
            double predicted = slope * i + intercept;
            ssTotal += Math.pow(values.get(i) - yMean, 2);
            ssResidual += Math.pow(values.get(i) - predicted, 2);
        }
        double rSquared = 1 - (ssResidual / ssTotal);
        
        // 生成预测
        List<Double> forecast = new ArrayList<>();
        for (int i = 0; i < PREDICTION_HORIZON_MINUTES; i++) {
            double predicted = slope * (n + i) + intercept;
            forecast.add(Math.max(0, predicted));
        }
        
        double confidence = Math.max(0.4, Math.min(0.85, rSquared * 0.8 + 0.2));
        
        return new ModelPrediction("LinearRegression", forecast, confidence, 0.3);
    }
    
    /**
     * Holt-Winters预测（带趋势）
     */
    private ModelPrediction holtWintersForecast(List<Double> values, double alpha, double beta) {
        int n = values.size();
        if (n < 3) {
            return linearRegressionForecast(values);
        }
        
        // 初始化水平和趋势
        double level = values.get(0);
        double trend = values.get(1) - values.get(0);
        
        // 应用Holt-Winters平滑
        for (int i = 1; i < n; i++) {
            double value = values.get(i);
            double newLevel = alpha * value + (1 - alpha) * (level + trend);
            double newTrend = beta * (newLevel - level) + (1 - beta) * trend;
            level = newLevel;
            trend = newTrend;
        }
        
        // 生成预测
        List<Double> forecast = new ArrayList<>();
        for (int i = 0; i < PREDICTION_HORIZON_MINUTES; i++) {
            double predicted = level + (i + 1) * trend;
            forecast.add(Math.max(0, predicted));
        }
        
        double confidence = Math.min(0.8, 0.4 + (n / 1000.0));
        
        return new ModelPrediction("HoltWinters", forecast, confidence, 0.2);
    }
    
    /**
     * 集成多个模型预测结果
     */
    private LoadForecast ensemblePredictions(String topic, int partition,
                                              List<ModelPrediction> predictions,
                                              List<Double> lagValues,
                                              List<Double> rateValues) {
        
        if (predictions.isEmpty()) {
            return createEmptyForecast(topic, partition);
        }
        
        // 归一化权重
        double totalWeight = predictions.stream()
            .mapToDouble(ModelPrediction::getWeight)
            .sum();
        
        // 加权平均预测
        List<Double> ensembleForecast = new ArrayList<>();
        List<Double> confidenceIntervals = new ArrayList<>();
        
        for (int t = 0; t < PREDICTION_HORIZON_MINUTES; t++) {
            double weightedSum = 0;
            double weightedConfidence = 0;
            
            for (ModelPrediction pred : predictions) {
                double normalizedWeight = pred.getWeight() / totalWeight;
                weightedSum += pred.getForecast().get(t) * normalizedWeight;
                weightedConfidence += pred.getConfidence() * normalizedWeight;
            }
            
            ensembleForecast.add(weightedSum);
            
            // 计算置信区间（随时间增加而扩大）
            double baseStd = calculateStdDev(lagValues);
            double timeDecay = 1 + (t / 10.0); // 时间衰减因子
            confidenceIntervals.add(baseStd * timeDecay);
        }
        
        // 计算整体置信度
        double overallConfidence = predictions.stream()
            .mapToDouble(ModelPrediction::getConfidence)
            .average()
            .orElse(0.5);
        
        // 检测异常趋势
        TrendType trend = detectTrend(ensembleForecast);
        
        // 计算峰值预测
        double peakLoad = ensembleForecast.stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0);
        
        int peakTime = ensembleForecast.indexOf(peakLoad);
        
        LoadForecast forecast = new LoadForecast();
        forecast.setTopic(topic);
        forecast.setPartition(partition);
        forecast.setGeneratedAt(Instant.now());
        forecast.setValidUntil(Instant.now().plus(Duration.ofMinutes(PREDICTION_HORIZON_MINUTES)));
        forecast.setPredictedLag(ensembleForecast);
        forecast.setConfidenceInterval(confidenceIntervals);
        forecast.setOverallConfidence(overallConfidence);
        forecast.setTrend(trend);
        forecast.setPeakLoad(peakLoad);
        forecast.setPeakTimeMinutes(peakTime);
        forecast.setUsedModels(predictions.stream()
            .map(ModelPrediction::getModelName)
            .collect(Collectors.toList()));
        
        return forecast;
    }
    
    /**
     * 创建空预测
     */
    private LoadForecast createEmptyForecast(String topic, int partition) {
        LoadForecast forecast = new LoadForecast();
        forecast.setTopic(topic);
        forecast.setPartition(partition);
        forecast.setGeneratedAt(Instant.now());
        forecast.setValidUntil(Instant.now().plus(Duration.ofMinutes(PREDICTION_HORIZON_MINUTES)));
        forecast.setPredictedLag(Collections.nCopies(PREDICTION_HORIZON_MINUTES, 0.0));
        forecast.setConfidenceInterval(Collections.nCopies(PREDICTION_HORIZON_MINUTES, 0.0));
        forecast.setOverallConfidence(0.0);
        forecast.setTrend(TrendType.STABLE);
        forecast.setPeakLoad(0.0);
        forecast.setPeakTimeMinutes(0);
        return forecast;
    }
    
    /**
     * 检测趋势类型
     */
    private TrendType detectTrend(List<Double> forecast) {
        if (forecast.size() < 5) {
            return TrendType.STABLE;
        }
        
        // 使用前后对比检测趋势
        double firstHalf = forecast.subList(0, forecast.size() / 2).stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
        
        double secondHalf = forecast.subList(forecast.size() / 2, forecast.size()).stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
        
        double change = (secondHalf - firstHalf) / Math.max(firstHalf, 1);
        
        if (change > 0.3) {
            return TrendType.RAPIDLY_INCREASING;
        } else if (change > 0.1) {
            return TrendType.INCREASING;
        } else if (change < -0.3) {
            return TrendType.RAPIDLY_DECREASING;
        } else if (change < -0.1) {
            return TrendType.DECREASING;
        } else {
            return TrendType.STABLE;
        }
    }
    
    /**
     * 计算趋势
     */
    private double calculateTrend(List<Double> values, int window) {
        if (values.size() < window * 2) {
            return 0;
        }
        
        double firstAvg = values.subList(values.size() - window * 2, values.size() - window)
            .stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        double secondAvg = values.subList(values.size() - window, values.size())
            .stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        return secondAvg - firstAvg;
    }
    
    /**
     * 计算标准差
     */
    private double calculateStdDev(List<Double> values) {
        if (values.isEmpty()) return 0;
        
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
        
        return Math.sqrt(variance);
    }
    
    /**
     * 评估模型性能
     */
    private void evaluateModelPerformance() {
        try {
            logger.debug("评估预测模型性能...");
            
            // 计算各模型的平均准确率
            Map<String, List<Double>> modelErrors = new HashMap<>();
            
            for (Map.Entry<String, ConcurrentHashMap<Integer, LoadForecast>> topicEntry : forecastCache.entrySet()) {
                String topic = topicEntry.getKey();
                
                for (Map.Entry<Integer, LoadForecast> partitionEntry : topicEntry.getValue().entrySet()) {
                    int partition = partitionEntry.getKey();
                    LoadForecast forecast = partitionEntry.getValue();
                    
                    // 获取实际值（如果预测已过期）
                    TimeSeries series = historicalData.getOrDefault(topic, new ConcurrentHashMap<>())
                        .get(partition);
                    
                    if (series != null && !series.getData().isEmpty()) {
                        // 计算预测误差
                        // 这里简化处理，实际应该对比预测值和实际值
                    }
                }
            }
            
            logger.debug("模型性能评估完成");
            
        } catch (Exception e) {
            logger.error("模型性能评估失败", e);
        }
    }
    
    // ============ 公共API ============
    
    /**
     * 获取分区预测
     */
    public Optional<LoadForecast> getForecast(String topic, int partition) {
        return Optional.ofNullable(
            forecastCache.getOrDefault(topic, new ConcurrentHashMap<>()).get(partition)
        );
    }
    
    /**
     * 获取Topic的所有预测
     */
    public Map<Integer, LoadForecast> getTopicForecasts(String topic) {
        return new HashMap<>(forecastCache.getOrDefault(topic, new ConcurrentHashMap<>()));
    }
    
    /**
     * 更新模型配置
     */
    public void updateModelConfig(PredictionModelConfig config) {
        modelConfig.set(config);
        logger.info("更新预测模型配置: {}", config);
    }
    
    // ============ 内部类 ============
    
    /**
     * 数据点
     */
    private static class DataPoint {
        private final long timestamp;
        private final long messageLag;
        private final long messageRate;
        private final double cpuUsage;
        private final double memoryUsage;
        
        public DataPoint(long timestamp, long messageLag, long messageRate, 
                         double cpuUsage, double memoryUsage) {
            this.timestamp = timestamp;
            this.messageLag = messageLag;
            this.messageRate = messageRate;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
        }
        
        public long getTimestamp() { return timestamp; }
        public long getMessageLag() { return messageLag; }
        public long getMessageRate() { return messageRate; }
        public double getCpuUsage() { return cpuUsage; }
        public double getMemoryUsage() { return memoryUsage; }
    }
    
    /**
     * 时间序列
     */
    private static class TimeSeries {
        private final Queue<DataPoint> data;
        private final int maxSize;
        
        public TimeSeries(int maxSize) {
            this.maxSize = maxSize;
            this.data = new ConcurrentLinkedQueue<>();
        }
        
        public synchronized void add(DataPoint point) {
            data.offer(point);
            while (data.size() > maxSize) {
                data.poll();
            }
        }
        
        public List<DataPoint> getData() {
            return new ArrayList<>(data);
        }
    }
    
    /**
     * 模型预测结果
     */
    private static class ModelPrediction {
        private final String modelName;
        private final List<Double> forecast;
        private final double confidence;
        private final double weight;
        
        public ModelPrediction(String modelName, List<Double> forecast, 
                               double confidence, double weight) {
            this.modelName = modelName;
            this.forecast = forecast;
            this.confidence = confidence;
            this.weight = weight;
        }
        
        public String getModelName() { return modelName; }
        public List<Double> getForecast() { return forecast; }
        public double getConfidence() { return confidence; }
        public double getWeight() { return weight; }
    }
    
    /**
     * 趋势类型
     */
    public enum TrendType {
        STABLE,           // 稳定
        INCREASING,       // 增长
        RAPIDLY_INCREASING, // 快速增长
        DECREASING,       // 下降
        RAPIDLY_DECREASING  // 快速下降
    }
    
    /**
     * 预测模型配置
     */
    public static class PredictionModelConfig {
        private boolean enableMovingAverage = true;
        private boolean enableExponentialSmoothing = true;
        private boolean enableLinearRegression = true;
        private boolean enableHoltWinters = true;
        private int movingAverageWindow = 5;
        private double smoothingAlpha = 0.3;
        private double hwAlpha = 0.3;
        private double hwBeta = 0.1;
        
        // Getters and setters...
        public boolean isEnableMovingAverage() { return enableMovingAverage; }
        public void setEnableMovingAverage(boolean v) { this.enableMovingAverage = v; }
        public boolean isEnableExponentialSmoothing() { return enableExponentialSmoothing; }
        public void setEnableExponentialSmoothing(boolean v) { this.enableExponentialSmoothing = v; }
        public boolean isEnableLinearRegression() { return enableLinearRegression; }
        public void setEnableLinearRegression(boolean v) { this.enableLinearRegression = v; }
        public boolean isEnableHoltWinters() { return enableHoltWinters; }
        public void setEnableHoltWinters(boolean v) { this.enableHoltWinters = v; }
        public int getMovingAverageWindow() { return movingAverageWindow; }
        public void setMovingAverageWindow(int v) { this.movingAverageWindow = v; }
        public double getSmoothingAlpha() { return smoothingAlpha; }
        public void setSmoothingAlpha(double v) { this.smoothingAlpha = v; }
        public double getHwAlpha() { return hwAlpha; }
        public void setHwAlpha(double v) { this.hwAlpha = v; }
        public double getHwBeta() { return hwBeta; }
        public void setHwBeta(double v) { this.hwBeta = v; }
    }
    
    /**
     * 模型性能指标
     */
    private static class ModelPerformanceMetrics {
        private final String modelName;
        private double mape = 0; // 平均绝对百分比误差
        private double rmse = 0; // 均方根误差
        private int predictionCount = 0;
        
        public ModelPerformanceMetrics(String modelName) {
            this.modelName = modelName;
        }
    }
}
