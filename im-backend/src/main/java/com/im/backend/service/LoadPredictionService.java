package com.im.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.concurrent.*;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket集群负载预测服务
 * 基于历史数据和时间序列分析进行智能负载预测
 */
@Service
public class LoadPredictionService {
    
    @Autowired
    private ClusterMetricsCollector metricsCollector;
    
    // 负载历史数据存储（节点ID -> 时间序列数据）
    private final Map<String, LinkedList<LoadDataPoint>> loadHistory = new ConcurrentHashMap<>();
    
    // 预测模型缓存
    private final Map<String, PredictionModel> predictionModels = new ConcurrentHashMap<>();
    
    // 预测线程池
    private final ExecutorService predictionExecutor = Executors.newFixedThreadPool(4);
    
    // 历史数据保留时长（1小时）
    private static final long HISTORY_RETENTION_MS = 60 * 60 * 1000;
    
    // 最小历史数据点数量
    private static final int MIN_HISTORY_POINTS = 10;
    
    // 预测时间窗口（分钟）
    private static final int PREDICTION_WINDOW_MINUTES = 10;
    
    // 负载阈值配置
    private static final double CPU_THRESHOLD_HIGH = 75.0;
    private static final double CPU_THRESHOLD_CRITICAL = 90.0;
    private static final double MEMORY_THRESHOLD_HIGH = 80.0;
    private static final double MEMORY_THRESHOLD_CRITICAL = 95.0;
    private static final double CONNECTIONS_THRESHOLD_HIGH = 0.8;
    private static final double CONNECTIONS_THRESHOLD_CRITICAL = 0.95;
    
    /**
     * 负载数据点
     */
    public static class LoadDataPoint {
        public final long timestamp;
        public final double cpuUsage;
        public final double memoryUsage;
        public final int activeConnections;
        public final int maxConnections;
        public final double networkIn;
        public final double networkOut;
        public final double messageRate;
        
        public LoadDataPoint(double cpuUsage, double memoryUsage, int activeConnections,
                            int maxConnections, double networkIn, double networkOut, double messageRate) {
            this.timestamp = System.currentTimeMillis();
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.activeConnections = activeConnections;
            this.maxConnections = maxConnections;
            this.networkIn = networkIn;
            this.networkOut = networkOut;
            this.messageRate = messageRate;
        }
        
        public double getConnectionRatio() {
            return maxConnections > 0 ? (double) activeConnections / maxConnections : 0;
        }
    }
    
    /**
     * 负载预测模型
     */
    public static class PredictionModel {
        private final String nodeId;
        private long lastUpdated;
        private double cpuTrend;
        private double memoryTrend;
        private double connectionTrend;
        private double seasonalFactor;
        
        // 线性回归参数
        private double slope;
        private double intercept;
        
        public PredictionModel(String nodeId) {
            this.nodeId = nodeId;
            this.lastUpdated = System.currentTimeMillis();
        }
        
        public void updateModel(List<LoadDataPoint> history) {
            if (history.size() < MIN_HISTORY_POINTS) {
                return;
            }
            
            // 计算趋势线（简单线性回归）
            calculateTrendLine(history);
            
            // 计算季节性因子
            calculateSeasonalFactor(history);
            
            this.lastUpdated = System.currentTimeMillis();
        }
        
        private void calculateTrendLine(List<LoadDataPoint> history) {
            int n = history.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
            
            long baseTime = history.get(0).timestamp;
            
            for (int i = 0; i < n; i++) {
                LoadDataPoint point = history.get(i);
                double x = (point.timestamp - baseTime) / 60000.0; // 分钟为单位
                double y = point.getConnectionRatio() * 100; // 连接使用率百分比
                
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumXX += x * x;
            }
            
            this.slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
            this.intercept = (sumY - slope * sumX) / n;
            this.connectionTrend = slope;
        }
        
        private void calculateSeasonalFactor(List<LoadDataPoint> history) {
            // 计算基于时间的季节性模式
            Map<Integer, List<Double>> hourlyPatterns = new HashMap<>();
            
            for (LoadDataPoint point : history) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(point.timestamp);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                
                hourlyPatterns.computeIfAbsent(hour, k -> new ArrayList<>())
                           .add(point.getConnectionRatio());
            }
            
            // 计算当前小时的平均负载因子
            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            List<Double> currentHourLoads = hourlyPatterns.get(currentHour);
            
            if (currentHourLoads != null && !currentHourLoads.isEmpty()) {
                double avg = currentHourLoads.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                this.seasonalFactor = avg;
            }
        }
        
        public LoadPrediction predict(int minutesAhead) {
            double predictedConnectionRatio = intercept + slope * minutesAhead;
            predictedConnectionRatio = Math.max(0, Math.min(100, predictedConnectionRatio));
            
            // 应用季节性调整
            predictedConnectionRatio *= (1 + seasonalFactor * 0.1);
            
            LoadPrediction prediction = new LoadPrediction();
            prediction.predictedConnectionUsage = predictedConnectionRatio;
            prediction.predictedCpuUsage = predictedConnectionRatio * 0.8; // 估算
            prediction.predictedMemoryUsage = predictedConnectionRatio * 0.9; // 估算
            prediction.confidence = calculateConfidence(minutesAhead);
            prediction.timestamp = System.currentTimeMillis() + minutesAhead * 60000;
            
            return prediction;
        }
        
        private double calculateConfidence(int minutesAhead) {
            // 预测越远，置信度越低
            double baseConfidence = 0.9;
            double timeDecay = minutesAhead * 0.02;
            return Math.max(0.3, baseConfidence - timeDecay);
        }
    }
    
    /**
     * 负载预测结果
     */
    public static class LoadPrediction {
        public long timestamp;
        public double predictedCpuUsage;
        public double predictedMemoryUsage;
        public double predictedConnectionUsage;
        public double confidence;
        public RiskLevel riskLevel = RiskLevel.NORMAL;
        public List<String> warnings = new ArrayList<>();
        
        public enum RiskLevel {
            NORMAL, ELEVATED, HIGH, CRITICAL
        }
        
        public void assessRisk() {
            int riskScore = 0;
            
            if (predictedCpuUsage >= CPU_THRESHOLD_CRITICAL) {
                riskScore += 3;
                warnings.add("CPU使用率预测超过临界值: " + String.format("%.1f%%", predictedCpuUsage));
            } else if (predictedCpuUsage >= CPU_THRESHOLD_HIGH) {
                riskScore += 2;
                warnings.add("CPU使用率预测超过高阈值: " + String.format("%.1f%%", predictedCpuUsage));
            }
            
            if (predictedMemoryUsage >= MEMORY_THRESHOLD_CRITICAL) {
                riskScore += 3;
                warnings.add("内存使用率预测超过临界值: " + String.format("%.1f%%", predictedMemoryUsage));
            } else if (predictedMemoryUsage >= MEMORY_THRESHOLD_HIGH) {
                riskScore += 2;
                warnings.add("内存使用率预测超过高阈值: " + String.format("%.1f%%", predictedMemoryUsage));
            }
            
            if (predictedConnectionUsage >= CONNECTIONS_THRESHOLD_CRITICAL * 100) {
                riskScore += 3;
                warnings.add("连接数预测超过临界值: " + String.format("%.1f%%", predictedConnectionUsage));
            } else if (predictedConnectionUsage >= CONNECTIONS_THRESHOLD_HIGH * 100) {
                riskScore += 2;
                warnings.add("连接数预测超过高阈值: " + String.format("%.1f%%", predictedConnectionUsage));
            }
            
            if (riskScore >= 7) {
                riskLevel = RiskLevel.CRITICAL;
            } else if (riskScore >= 4) {
                riskLevel = RiskLevel.HIGH;
            } else if (riskScore >= 2) {
                riskLevel = RiskLevel.ELEVATED;
            } else {
                riskLevel = RiskLevel.NORMAL;
            }
        }
    }
    
    /**
     * 集群负载预测结果
     */
    public static class ClusterPrediction {
        public long timestamp;
        public Map<String, LoadPrediction> nodePredictions = new HashMap<>();
        public double overallRiskScore;
        public ScalingRecommendation recommendation;
        public List<String> alerts = new ArrayList<>();
        
        public enum ScalingRecommendation {
            SCALE_UP_IMMEDIATE,    // 立即扩容
            SCALE_UP_RECOMMENDED,  // 建议扩容
            MAINTAIN,              // 保持现状
            SCALE_DOWN_CANDIDATE,  // 可考虑缩容
            SCALE_DOWN_RECOMMENDED // 建议缩容
        }
        
        public void generateRecommendation() {
            int criticalCount = 0;
            int highCount = 0;
            int elevatedCount = 0;
            double avgConnectionUsage = 0;
            
            for (LoadPrediction pred : nodePredictions.values()) {
                avgConnectionUsage += pred.predictedConnectionUsage;
                switch (pred.riskLevel) {
                    case CRITICAL:
                        criticalCount++;
                        break;
                    case HIGH:
                        highCount++;
                        break;
                    case ELEVATED:
                        elevatedCount++;
                        break;
                }
            }
            
            if (!nodePredictions.isEmpty()) {
                avgConnectionUsage /= nodePredictions.size();
            }
            
            if (criticalCount > 0) {
                recommendation = ScalingRecommendation.SCALE_UP_IMMEDIATE;
                alerts.add("检测到" + criticalCount + "个节点预测负载为CRITICAL");
            } else if (highCount > 0 || elevatedCount >= 2) {
                recommendation = ScalingRecommendation.SCALE_UP_RECOMMENDED;
                alerts.add("检测到" + highCount + "个节点高负载，建议扩容");
            } else if (avgConnectionUsage < 30) {
                recommendation = ScalingRecommendation.SCALE_DOWN_RECOMMENDED;
                alerts.add("平均连接使用率低于30%，建议缩容");
            } else if (avgConnectionUsage < 50) {
                recommendation = ScalingRecommendation.SCALE_DOWN_CANDIDATE;
            } else {
                recommendation = ScalingRecommendation.MAINTAIN;
            }
            
            this.overallRiskScore = calculateOverallRisk();
        }
        
        private double calculateOverallRisk() {
            if (nodePredictions.isEmpty()) return 0;
            
            double totalRisk = 0;
            for (LoadPrediction pred : nodePredictions.values()) {
                double riskWeight = 0;
                switch (pred.riskLevel) {
                    case CRITICAL:
                        riskWeight = 1.0;
                        break;
                    case HIGH:
                        riskWeight = 0.7;
                        break;
                    case ELEVATED:
                        riskWeight = 0.4;
                        break;
                    case NORMAL:
                        riskWeight = 0.1;
                        break;
                }
                totalRisk += riskWeight * pred.confidence;
            }
            
            return totalRisk / nodePredictions.size();
        }
    }
    
    /**
     * 记录负载数据点
     */
    public void recordLoadData(String nodeId, LoadDataPoint dataPoint) {
        LinkedList<LoadDataPoint> history = loadHistory.computeIfAbsent(nodeId, k -> new LinkedList<>());
        
        synchronized (history) {
            history.addLast(dataPoint);
            
            // 清理过期数据
            long cutoff = System.currentTimeMillis() - HISTORY_RETENTION_MS;
            while (!history.isEmpty() && history.getFirst().timestamp < cutoff) {
                history.removeFirst();
            }
            
            // 限制最大数据点数
            while (history.size() > 360) { // 1小时，每10秒一个点
                history.removeFirst();
            }
        }
    }
    
    /**
     * 更新预测模型
     */
    public void updatePredictionModel(String nodeId) {
        LinkedList<LoadDataPoint> history = loadHistory.get(nodeId);
        if (history == null || history.size() < MIN_HISTORY_POINTS) {
            return;
        }
        
        PredictionModel model = predictionModels.computeIfAbsent(nodeId, PredictionModel::new);
        
        List<LoadDataPoint> historyCopy;
        synchronized (history) {
            historyCopy = new ArrayList<>(history);
        }
        
        model.updateModel(historyCopy);
    }
    
    /**
     * 预测单个节点未来负载
     */
    public LoadPrediction predictNodeLoad(String nodeId, int minutesAhead) {
        PredictionModel model = predictionModels.get(nodeId);
        if (model == null) {
            return null;
        }
        
        LoadPrediction prediction = model.predict(minutesAhead);
        prediction.assessRisk();
        return prediction;
    }
    
    /**
     * 预测整个集群负载
     */
    public ClusterPrediction predictClusterLoad(int minutesAhead) {
        ClusterPrediction clusterPrediction = new ClusterPrediction();
        clusterPrediction.timestamp = System.currentTimeMillis();
        
        Set<String> nodeIds = new HashSet<>(loadHistory.keySet());
        nodeIds.addAll(predictionModels.keySet());
        
        for (String nodeId : nodeIds) {
            LoadPrediction prediction = predictNodeLoad(nodeId, minutesAhead);
            if (prediction != null) {
                clusterPrediction.nodePredictions.put(nodeId, prediction);
            }
        }
        
        clusterPrediction.generateRecommendation();
        return clusterPrediction;
    }
    
    /**
     * 获取多时间点预测（用于趋势分析）
     */
    public Map<Integer, ClusterPrediction> predictMultiTimePoints(int[] minutesAheadList) {
        Map<Integer, ClusterPrediction> predictions = new LinkedHashMap<>();
        
        for (int minutes : minutesAheadList) {
            predictions.put(minutes, predictClusterLoad(minutes));
        }
        
        return predictions;
    }
    
    /**
     * 检查是否需要立即扩容
     */
    public boolean isScaleUpUrgent() {
        ClusterPrediction prediction5Min = predictClusterLoad(5);
        ClusterPrediction prediction10Min = predictClusterLoad(10);
        
        boolean urgent5Min = prediction5Min.recommendation == 
            ClusterPrediction.ScalingRecommendation.SCALE_UP_IMMEDIATE;
        boolean urgent10Min = prediction10Min.recommendation == 
            ClusterPrediction.ScalingRecommendation.SCALE_UP_IMMEDIATE;
        
        return urgent5Min || urgent10Min;
    }
    
    /**
     * 检查是否可以缩容
     */
    public boolean isScaleDownCandidate() {
        ClusterPrediction prediction10Min = predictClusterLoad(10);
        ClusterPrediction prediction20Min = predictClusterLoad(20);
        
        boolean candidate10Min = prediction10Min.recommendation == 
            ClusterPrediction.ScalingRecommendation.SCALE_DOWN_RECOMMENDED ||
            prediction10Min.recommendation == ClusterPrediction.ScalingRecommendation.SCALE_DOWN_CANDIDATE;
        boolean candidate20Min = prediction20Min.recommendation == 
            ClusterPrediction.ScalingRecommendation.SCALE_DOWN_RECOMMENDED;
        
        return candidate10Min && candidate20Min;
    }
    
    /**
     * 获取负载趋势分析
     */
    public LoadTrendAnalysis analyzeLoadTrend(String nodeId) {
        LinkedList<LoadDataPoint> history = loadHistory.get(nodeId);
        if (history == null || history.size() < MIN_HISTORY_POINTS) {
            return null;
        }
        
        LoadTrendAnalysis analysis = new LoadTrendAnalysis();
        analysis.nodeId = nodeId;
        
        List<LoadDataPoint> historyCopy;
        synchronized (history) {
            historyCopy = new ArrayList<>(history);
        }
        
        // 计算趋势
        double[] connectionRatios = historyCopy.stream()
            .mapToDouble(LoadDataPoint::getConnectionRatio)
            .toArray();
        
        analysis.trendDirection = calculateTrendDirection(connectionRatios);
        analysis.trendStrength = calculateTrendStrength(connectionRatios);
        analysis.volatility = calculateVolatility(connectionRatios);
        analysis.peakHours = identifyPeakHours(historyCopy);
        
        return analysis;
    }
    
    private String calculateTrendDirection(double[] values) {
        if (values.length < 2) return "STABLE";
        
        double first = values[0];
        double last = values[values.length - 1];
        double change = (last - first) / first * 100;
        
        if (change > 10) return "RAPIDLY_INCREASING";
        if (change > 5) return "INCREASING";
        if (change < -10) return "RAPIDLY_DECREASING";
        if (change < -5) return "DECREASING";
        return "STABLE";
    }
    
    private double calculateTrendStrength(double[] values) {
        if (values.length < 2) return 0;
        
        double sum = 0;
        for (int i = 1; i < values.length; i++) {
            sum += Math.abs(values[i] - values[i-1]);
        }
        
        return sum / (values.length - 1);
    }
    
    private double calculateVolatility(double[] values) {
        if (values.length < 2) return 0;
        
        double mean = Arrays.stream(values).average().orElse(0);
        double variance = Arrays.stream(values)
            .map(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0);
        
        return Math.sqrt(variance);
    }
    
    private List<Integer> identifyPeakHours(List<LoadDataPoint> history) {
        Map<Integer, Double> hourlyLoads = new HashMap<>();
        Map<Integer, Integer> hourlyCounts = new HashMap<>();
        
        for (LoadDataPoint point : history) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(point.timestamp);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            
            hourlyLoads.merge(hour, point.getConnectionRatio(), Double::sum);
            hourlyCounts.merge(hour, 1, Integer::sum);
        }
        
        // 计算平均负载
        Map<Integer, Double> hourlyAvg = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : hourlyLoads.entrySet()) {
            int hour = entry.getKey();
            double avg = entry.getValue() / hourlyCounts.get(hour);
            hourlyAvg.put(hour, avg);
        }
        
        // 找出高峰时段（负载高于平均值20%）
        double overallAvg = hourlyAvg.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
        
        List<Integer> peakHours = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : hourlyAvg.entrySet()) {
            if (entry.getValue() > overallAvg * 1.2) {
                peakHours.add(entry.getKey());
            }
        }
        
        Collections.sort(peakHours);
        return peakHours;
    }
    
    /**
     * 负载趋势分析结果
     */
    public static class LoadTrendAnalysis {
        public String nodeId;
        public String trendDirection;
        public double trendStrength;
        public double volatility;
        public List<Integer> peakHours;
        public String summary;
    }
    
    /**
     * 清理过期数据
     */
    public void cleanupOldData() {
        long cutoff = System.currentTimeMillis() - HISTORY_RETENTION_MS;
        
        for (LinkedList<LoadDataPoint> history : loadHistory.values()) {
            synchronized (history) {
                history.removeIf(point -> point.timestamp < cutoff);
            }
        }
        
        // 清理不再活跃的节点模型
        predictionModels.entrySet().removeIf(entry -> {
            LinkedList<LoadDataPoint> history = loadHistory.get(entry.getKey());
            return history == null || history.isEmpty();
        });
    }
    
    /**
     * 获取所有节点历史数据概览
     */
    public Map<String, Integer> getHistoryOverview() {
        Map<String, Integer> overview = new HashMap<>();
        for (Map.Entry<String, LinkedList<LoadDataPoint>> entry : loadHistory.entrySet()) {
            synchronized (entry.getValue()) {
                overview.put(entry.getKey(), entry.getValue().size());
            }
        }
        return overview;
    }
}