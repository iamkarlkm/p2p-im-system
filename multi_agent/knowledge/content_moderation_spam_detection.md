# 消息内容审核与反垃圾系统

## 概述

即时通讯（IM）系统中的内容审核与反垃圾系统是确保平台健康运营的关键组件。一个完善的内容审核系统需要处理文本、图片、视频、音频等多种内容形式，同时兼顾实时性、准确性和用户体验。

## 系统架构

### 分层架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                      客户端层 (Client)                       │
├─────────────────────────────────────────────────────────────┤
│  本地敏感词过滤 │ 输入框实时检测 │ 用户举报入口               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Gateway)                        │
├─────────────────────────────────────────────────────────────┤
│  请求验证 │ 频率限制 │ IP/设备黑名单 │ 初步内容过滤          │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    消息服务层 (Message Service)              │
├─────────────────────────────────────────────────────────────┤
│  消息路由 │ 内容分发 │ 撤回处理 │ 审核状态追踪               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   审核服务层 (Moderation Service)            │
├─────────────────────────────────────────────────────────────┤
│  文本审核 │ 图片审核 │ 视频审核 │ 音频审核 │ AI模型推理     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    存储层 (Storage)                         │
├─────────────────────────────────────────────────────────────┤
│  敏感词库 │ 审核记录 │ 违规记录 │ 机器学习模型               │
└─────────────────────────────────────────────────────────────┘
```

### 异步审核 vs 同步审核

**同步审核（Sync Mode）**：
- 适用于高风险内容（涉政、涉黄、涉暴）
- 消息发送前必须通过审核
- 延迟较高（通常 100-500ms）
- 失败时拒绝消息发送

**异步审核（Async Mode）**：
- 适用于普通垃圾广告
- 消息先发送，后台异步审核
- 低延迟（<10ms）
- 审核后对消息进行降权/撤回处理

**混合模式（Hybrid Mode）**：
- 高风险词直接同步拦截
- 普通内容异步审核
- AI模型实时推理 + 人工复核

## 文本内容审核

### 敏感词过滤

#### 1. 基础敏感词库

```java
// 敏感词分类
public enum SensitiveWordCategory {
    POLITICAL("涉政"),           // 政治敏感
    PORNOGRAPHIC("涉黄"),        // 色情低俗
    VIOLENT("涉暴"),            // 暴力恐怖
    GAMBLING("赌博"),            // 赌博诈骗
    DRUGS("涉毒"),              // 毒品相关
    TERRORISM("涉恐"),          // 恐怖主义
    HATE_SPEECH("仇恨言论"),     // 种族歧视
    PERSONAL_INFO("个人信息"),   // 隐私泄露
    ADVERTISING("广告"),         // 垃圾广告
    OTHER("其他");               // 其他违规
}

// 敏感词实体
public class SensitiveWord {
    private Long id;
    private String word;
    private SensitiveWordCategory category;
    private Integer level;              // 严重程度 1-5
    private Boolean isRegex;            // 是否正则表达式
    private Boolean isEnabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

#### 2. 多模式匹配算法

**DFA（确定有限自动机）算法**：
```java
public class DFAFilter {
    private Map<Object, Object> wordMap = new HashMap<>();
    
    // 构建DFA状态机
    public void addWord(String word) {
        Map<Object, Object> current = wordMap;
        for (char c : word.toCharArray()) {
            Object obj = current.get(c);
            if (obj == null) {
                Map<Object, Object> newMap = new HashMap<>();
                newMap.put("isEnd", false);
                current.put(c, newMap);
                current = newMap;
            } else {
                current = (Map<Object, Object>) obj;
            }
        }
        current.put("isEnd", true);
    }
    
    // 检测敏感词
    public List<String> filter(String text) {
        List<String> foundWords = new ArrayList<>();
        Map<Object, Object> current = wordMap;
        int start = -1;
        int length = 0;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Object obj = current.get(c);
            
            if (obj == null) {
                if (start != -1) {
                    i = start;
                    start = -1;
                    current = wordMap;
                }
            } else {
                if (start == -1) {
                    start = i;
                }
                if (obj instanceof Map) {
                    current = (Map<Object, Object>) obj;
                    length++;
                    if (Boolean.TRUE.equals(current.get("isEnd"))) {
                        foundWords.add(text.substring(start, start + length));
                        start = -1;
                        current = wordMap;
                        length = 0;
                    }
                }
            }
        }
        return foundWords;
    }
}
```

**AC自动机（Aho-Corasick）算法**：
- 适用于大规模敏感词库（>100万词）
- 时间复杂度 O(n+m)，n为文本长度，m为匹配次数
- 支持批量模式匹配

**Trie树 + Hash优化**：
```java
public class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isEnd;
    private Long wordId;
    private Integer category;
    private Integer level;
}

public class OptimizedDFAFilter {
    private final TrieNode root = new TrieNode();
    private final ConcurrentHashMap<String, Boolean> cache = new ConcurrentHashMap<>();
    
    // 构建前缀树
    public void build(List<SensitiveWord> words) {
        for (SensitiveWord word : words) {
            TrieNode current = root;
            for (char c : word.getWord().toCharArray()) {
                current = current.getChildren()
                    .computeIfAbsent(c, k -> new TrieNode());
            }
            current.setEnd(true);
            current.setWordId(word.getId());
            current.setCategory(word.getCategory().getCode());
            current.setLevel(word.getLevel());
        }
    }
}
```

#### 3. 变体词识别

中文变体词的识别是难点，需要处理：
- **谐音替换**：「微信」→ 「威信」「微xin」
- **简繁体转换**：「微信」↔ 「微信」
- **特殊符号干扰**：「微★信」「微~信」
- **拼音首字母**：「WX」「w x」
- **emoji符号**：「微📱信」「微✨信」

```java
public class VariantWordRecognizer {
    // 拼音转换映射
    private static final Map<Character, String> PINYIN_MAP;
    
    // 检测变体词
    public boolean containsVariant(String text, String target) {
        // 1. 去除特殊符号
        String cleaned = removeSpecialChars(text);
        
        // 2. 中文拼音转换
        String pinyin = cnToPinyin(cleaned);
        
        // 3. 简繁体转换
        String simplified = toSimplifiedChinese(cleaned);
        
        // 4. 匹配检测
        return pinyin.contains(getPinyin(target)) 
            || simplified.contains(target);
    }
    
    // 同音字检测
    public boolean containsHomophone(String text, String target) {
        String targetPinyin = getPinyin(target);
        String textPinyin = cnToPinyin(text);
        return fuzzyMatch(targetPinyin, textPinyin);
    }
}
```

### 正则表达式审核

```java
public class RegexModeration {
    // 常见违规正则模式
    private static final List<RegexPattern> PATTERNS = Arrays.asList(
        // 手机号
        new RegexPattern("1[3-9]\\d{9}", SpamType.PHONE_NUMBER),
        // QQ号
        new RegexPattern("[1-9]\\d{4,10}", SpamType.QQ_NUMBER),
        // 网址
        new RegexPattern("(https?://|www\\.)[^\\s]+", SpamType.URL),
        // 邮箱
        new RegexPattern("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", SpamType.EMAIL),
        // 微信号
        new RegexPattern("[微V][\\s:：-]*[a-zA-Z][a-zA-Z0-9_-]{5,19}", SpamType.WECHAT_ID),
        // 诱导外链话术
        new RegexPattern("(加我|加vx|加微信|私聊|看主页|点击下方)", SpamType.EXTERNAL_GUIDE)
    );
    
    public List<MatchResult> match(String text) {
        List<MatchResult> results = new ArrayList<>();
        for (RegexPattern pattern : PATTERNS) {
            Matcher matcher = pattern.getPattern().matcher(text);
            while (matcher.find()) {
                results.add(new MatchResult(
                    matcher.group(),
                    pattern.getType(),
                    matcher.start(),
                    matcher.end()
                ));
            }
        }
        return results;
    }
}
```

### AI文本审核

#### 1. 文本分类模型

基于深度学习的文本分类用于识别：
- 色情内容
- 暴力恐怖
- 政治敏感
- 垃圾广告
- 仇恨言论
- 诈骗信息

```python
# PyTorch 文本分类模型示例
import torch
import torch.nn as nn
from transformers import BertModel, BertTokenizer

class TextModerationClassifier(nn.Module):
    def __init__(self, num_classes=6):
        super().__init__()
        self.bert = BertModel.from_pretrained('bert-base-chinese')
        self.classifier = nn.Sequential(
            nn.Dropout(0.1),
            nn.Linear(768, 256),
            nn.ReLU(),
            nn.Dropout(0.1),
            nn.Linear(256, num_classes)
        )
        
    def forward(self, input_ids, attention_mask):
        outputs = self.bert(
            input_ids=input_ids,
            attention_mask=attention_mask
        )
        pooled_output = outputs.pooler_output
        logits = self.classifier(pooled_output)
        return logits

class ModerationService:
    def __init__(self):
        self.model = TextModerationClassifier()
        self.model.load_state_dict(torch.load('moderation_model.pth'))
        self.model.eval()
        self.tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
        
    async def moderate_text(self, text: str) -> ModerationResult:
        inputs = self.tokenizer(
            text,
            max_length=512,
            padding='max_length',
            truncation=True,
            return_tensors='pt'
        )
        
        with torch.no_grad():
            logits = self.model(
                inputs['input_ids'],
                inputs['attention_mask']
            )
            probs = torch.sigmoid(logits)[0]
            
        categories = ['porn', 'violence', 'politics', 'spam', 'hate', 'fraud']
        violations = []
        
        for i, prob in enumerate(probs):
            if prob > 0.7:  # 阈值
                violations.append({
                    'category': categories[i],
                    'confidence': prob.item(),
                    'level': 'high' if prob > 0.9 else 'medium'
                })
                
        return ModerationResult(
            passed=len(violations) == 0,
            violations=violations
        )
```

#### 2. 文本相似度检测

```java
public class SemanticSimilarity {
    // 使用SimHash进行相似度检测
    public String computeSimHash(String text) {
        // 1. 分词
        List<String> tokens = segment(text);
        
        // 2. TF-IDF权重计算
        Map<String, Double> weights = computeTFIDF(tokens);
        
        // 3. 哈希并加权
        long[] hashBits = new long[256];
        for (String token : tokens) {
            String hash = md5(token);
            double weight = weights.getOrDefault(token, 1.0);
            
            for (int i = 0; i < 256; i++) {
                if (hash.charAt(i / 4) > '7') {
                    hashBits[i] += weight;
                } else {
                    hashBits[i] -= weight;
                }
            }
        }
        
        // 4. 生成SimHash
        StringBuilder simHash = new StringBuilder();
        for (long bit : hashBits) {
            simHash.append(bit > 0 ? '1' : '0');
        }
        return simHash.toString();
    }
    
    // 海明距离计算
    public int hammingDistance(String hash1, String hash2) {
        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
    
    // 判断是否为相似内容（海明距离<3）
    public boolean isSimilar(String text1, String text2) {
        String hash1 = computeSimHash(text1);
        String hash2 = computeSimHash(text2);
        return hammingDistance(hash1, hash2) < 3;
    }
}
```

## 图片内容审核

### 多层审核策略

```
图片审核流程
     ↓
┌─────────────────┐
│  1. 敏感词OCR识别 │
└─────────────────┘
     ↓
┌─────────────────┐
│  2. 肤色检测     │──高肤色比例──→ 色情审核
└─────────────────┘
     ↓
┌─────────────────┐
│  3. 场景识别     │──涉政/涉暴/敏感场景──→ 人工复核
└─────────────────┘
     ↓
┌─────────────────┐
│  4. 物体检测     │──违禁物品/特殊符号──→ 人工复核
└─────────────────┘
     ↓
┌─────────────────┐
│  5. 图片鉴伪     │──PS/AI生成──→ 特殊处理
└─────────────────┘
     ↓
    通过/拒绝
```

### 图片审核服务实现

```java
@Service
public class ImageModerationService {
    
    @Autowired
    private SkinDetector skinDetector;
    
    @Autowired
    private SceneClassifier sceneClassifier;
    
    @Autowired
    private ObjectDetector objectDetector;
    
    @Autowired
    private ImageForensicsService forensicsService;
    
    public ImageModerationResult moderate(byte[] imageData) {
        ImageModerationResult result = new ImageModerationResult();
        
        try {
            // 1. 基础检查：图片尺寸、格式
            ImageInfo info = extractImageInfo(imageData);
            if (info.getWidth() > 10000 || info.getHeight() > 10000) {
                result.reject("图片尺寸过大");
                return result;
            }
            
            // 2. OCR敏感词检测
            String ocrText = ocrService.extractText(imageData);
            List<String> sensitiveWords = dfaFilter.filter(ocrText);
            if (!sensitiveWords.isEmpty()) {
                result.addViolation(new Violation("OCR敏感词", sensitiveWords));
            }
            
            // 3. 肤色检测（快速过滤）
            double skinRatio = skinDetector.detect(imageData);
            if (skinRatio > 0.4) {
                result.setNeedDeepCheck(true);
            }
            
            // 4. 深度学习模型审核
            if (result.needDeepCheck()) {
                ImageClassificationResult classification = imageModel.predict(imageData);
                
                if (classification.hasCategory("porn")) {
                    result.addViolation(new Violation("色情内容", 
                        classification.getConfidence("porn")));
                }
                
                if (classification.hasCategory("violence")) {
                    result.addViolation(new Violation("暴力内容",
                        classification.getConfidence("violence")));
                }
                
                if (classification.hasCategory("political")) {
                    result.addViolation(new Violation("政治敏感",
                        classification.getConfidence("political")));
                }
            }
            
            // 5. 图片鉴伪
            boolean isAIGenerated = forensicsService.detectAI(imageData);
            if (isAIGenerated) {
                result.addWarning("疑似AI生成图片");
            }
            
            // 6. 汇总判断
            if (result.hasHighSeverityViolation()) {
                result.reject("严重违规内容");
            } else if (result.hasMediumSeverityViolation()) {
                result.pending("需要人工复核");
            } else {
                result.pass();
            }
            
        } catch (Exception e) {
            log.error("图片审核异常", e);
            result.error("审核服务异常");
        }
        
        return result;
    }
}
```

### 肤色检测算法

```java
public class SkinDetector {
    
    // YCbCr色彩空间肤色范围
    private static final double CB_MIN = 77.0;
    private static final double CB_MAX = 127.0;
    private static final double CR_MIN = 133.0;
    private static final double CR_MAX = 173.0;
    
    public double detect(byte[] imageData) {
        BufferedImage image = readImage(imageData);
        int skinPixels = 0;
        int totalPixels = image.getWidth() * image.getHeight();
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                if (isSkinColor(r, g, b)) {
                    skinPixels++;
                }
            }
        }
        
        return (double) skinPixels / totalPixels;
    }
    
    private boolean isSkinColor(int r, int g, int b) {
        // RGB转YCbCr
        double y = 0.299 * r + 0.587 * g + 0.114 * b;
        double cb = 128 - 0.168736 * r - 0.331264 * g + 0.5 * b;
        double cr = 128 + 0.5 * r - 0.418688 * g - 0.081312 * b;
        
        return cb >= CB_MIN && cb <= CB_MAX
            && cr >= CR_MIN && cr <= CR_MAX;
    }
}
```

## 视频/音频内容审核

### 视频审核流程

```java
@Service
public class VideoModerationService {
    
    public VideoModerationResult moderate(byte[] videoData) {
        VideoModerationResult result = new VideoModerationResult();
        
        // 1. 提取关键帧（每秒1帧）
        List<BufferedImage> keyframes = extractKeyframes(videoData, 1);
        
        // 2. 并行审核关键帧
        List<CompletableFuture<ImageModerationResult>> futures = 
            keyframes.parallelStream()
                .map(frame -> CompletableFuture.supplyAsync(
                    () -> imageModerationService.moderate(frame)))
                .collect(Collectors.toList());
        
        // 3. 音频审核
        byte[] audioData = extractAudio(videoData);
        AudioModerationResult audioResult = audioModerationService.moderate(audioData);
        
        // 4. 综合判断
        List<ImageModerationResult> frameResults = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        // 取最严重的结果
        ImageModerationResult worstFrame = frameResults.stream()
            .max(Comparator.comparing(ImageModerationResult::getSeverity))
            .orElse(ImageModerationResult.PASS);
        
        if (worstFrame.isRejected() || audioResult.isRejected()) {
            result.reject("内容违规");
            result.setDetails(worstFrame);
        } else if (worstFrame.isPending() || audioResult.isPending()) {
            result.pending("需要人工复核");
        } else {
            result.pass();
        }
        
        return result;
    }
}
```

### 音频审核

```java
@Service
public class AudioModerationService {
    
    public AudioModerationResult moderate(byte[] audioData) {
        AudioModerationResult result = new AudioModerationResult();
        
        // 1. 语音识别
        String text = speechRecognition.recognize(audioData);
        
        // 2. 文本审核
        TextModerationResult textResult = textModerationService.moderate(text);
        if (textResult.hasViolation()) {
            result.addViolation(textResult.getViolations());
        }
        
        // 3. 声纹分析（敏感人物声纹检测）
        List<String> speakerIds = speakerRecognition.identify(audioData);
        for (String speakerId : speakerIds) {
            if (sensitiveSpeakerRepository.exists(speakerId)) {
                result.addViolation(new Violation("敏感人物声纹", speakerId));
            }
        }
        
        // 4. 音频特征分析
        AudioFeatures features = audioAnalyzer.extractFeatures(audioData);
        if (features.hasSuspiciousPatterns()) {
            result.addWarning("可疑音频模式");
        }
        
        return result;
    }
}
```

## 实时反垃圾策略

### 用户行为分析

```java
@Service
public class AntiSpamService {
    
    @Autowired
    private UserBehaviorRepository behaviorRepo;
    
    // 检测垃圾用户
    public SpamUserResult detectSpamUser(Long userId) {
        UserBehavior behavior = behaviorRepo.findByUserId(userId);
        
        SpamUserResult result = new SpamUserResult();
        
        // 1. 发送频率检测
        if (behavior.getMsgPerMinute() > 10) {
            result.addScore(30, "发送频率异常");
        }
        
        // 2. 相似内容检测
        if (behavior.getSimilarContentRatio() > 0.8) {
            result.addScore(40, "内容重复率高");
        }
        
        // 3. 新好友发送率
        if (behavior.getNewFriendMsgRatio() > 0.9) {
            result.addScore(25, "新好友发送率高");
        }
        
        // 4. 内容相似度
        double contentSimilarity = behavior.getContentSimilarity();
        if (contentSimilarity > 0.9) {
            result.addScore(50, "消息内容高度相似");
        }
        
        // 5. 链接/二维码发送
        if (behavior.getLinkShareCount() > 50) {
            result.addScore(20, "链接分享过多");
        }
        
        // 综合评分
        if (result.getTotalScore() >= 80) {
            result.setLevel(SpamLevel.HIGH);
        } else if (result.getTotalScore() >= 50) {
            result.setLevel(SpamLevel.MEDIUM);
        } else {
            result.setLevel(SpamLevel.LOW);
        }
        
        return result;
    }
}
```

### 群发控制策略

```java
@Configuration
public class AntiSpamConfig {
    
    @Bean
    public RateLimiter messageRateLimiter() {
        return RateLimiter.custom()
            .limitForPeriod(100)           // 每分钟最多100条
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofMillis(100))
            .build();
    }
    
    @Bean
    public RateLimiter friendRequestRateLimiter() {
        return RateLimiter.custom()
            .limitForPeriod(20)            // 每分钟最多20个好友请求
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofMillis(100))
            .build();
    }
}

// 消息发送拦截
@Aspect
@Component
public class AntiSpamAspect {
    
    @Around("@annotation(MessageRateLimited)")
    public Object checkMessageRate(ProceedingJoinPoint point) throws Throwable {
        Long userId = getCurrentUserId();
        
        // 检查消息发送频率
        RateLimiterResult result = messageRateLimiter.tryAcquire(userId);
        if (!result.isAllowed()) {
            throw new SpamDetectedException("发送消息过于频繁");
        }
        
        // 检查相似内容
        Message message = (Message) point.getArgs()[0];
        if (isDuplicateContent(userId, message.getContent())) {
            throw new SpamDetectedException("疑似重复内容");
        }
        
        return point.proceed();
    }
}
```

## 机器学习模型

### 特征工程

```python
class SpamFeatureExtractor:
    def extract(self, message: str, user: User) -> List[float]:
        features = []
        
        # 文本特征
        features.append(self.text_length(message))           # 文本长度
        features.append(self.has_url(message))               # 是否含链接
        features.append(self.has_phone(message))             # 是否含手机号
        features.append(self.has_email(message))             # 是否含邮箱
        features.append(self.url_count(message))             # 链接数量
        features.append(self.emoji_ratio(message))            # emoji占比
        features.append(self.special_char_ratio(message))    # 特殊字符占比
        features.append(self.digit_ratio(message))           # 数字占比
        
        # 用户特征
        features.append(user.msg_per_minute)                  # 发送频率
        features.append(user.friend_count)                   # 好友数量
        features.append(user.account_age_days)                # 账号年龄
        features.append(user.is_verified)                    # 是否认证
        features.append(user.msg_count_today)                # 今日消息数
        
        # 上下文特征
        features.append(self.similar_to_history(message))     # 与历史消息相似度
        
        return features
    
    def similar_to_history(self, message: str) -> float:
        # 计算与最近100条消息的相似度
        history = get_user_recent_messages(user_id, limit=100)
        if not history:
            return 0.0
        max_similarity = max(
            self.cosine_similarity(message, h.content)
            for h in history
        )
        return max_similarity
```

### 模型训练与更新

```python
import torch
from torch.utils.data import DataLoader

class SpamDetectionTrainer:
    def __init__(self):
        self.model = SpamClassifier(input_dim=50, hidden_dim=128, output_dim=2)
        self.optimizer = torch.optim.Adam(self.model.parameters(), lr=0.001)
        self.criterion = nn.CrossEntropyLoss()
        
    def train(self, train_data, epochs=10):
        train_loader = DataLoader(train_data, batch_size=64, shuffle=True)
        
        for epoch in range(epochs):
            total_loss = 0
            for features, labels in train_loader:
                self.optimizer.zero_grad()
                outputs = self.model(features)
                loss = self.criterion(outputs, labels)
                loss.backward()
                self.optimizer.step()
                total_loss += loss.item()
                
            print(f"Epoch {epoch+1}, Loss: {total_loss/len(train_loader)}")
            
    def online_update(self, new_samples):
        """增量学习，持续优化模型"""
        for features, label in new_samples:
            self.optimizer.zero_grad()
            output = self.model(features.unsqueeze(0))
            loss = self.criterion(output, label.unsqueeze(0))
            loss.backward()
            self.optimizer.step()
```

## 人工审核系统

### 审核工作台

```java
@RestController
@RequestMapping("/api/moderation")
public class ModerationController {
    
    @Autowired
    private ModerationTaskRepository taskRepository;
    
    // 获取待审核任务
    @GetMapping("/tasks")
    public List<ModerationTask> getPendingTasks(
            @RequestParam(defaultValue = "10") int limit) {
        return taskRepository.findPendingTasks(limit);
    }
    
    // 提交审核结果
    @PostMapping("/tasks/{taskId}/review")
    public void submitReview(
            @PathVariable Long taskId,
            @RequestBody ModerationReview review) {
        ModerationTask task = taskRepository.findById(taskId);
        task.setStatus(Status.COMPLETED);
        task.setResult(review.getResult());
        task.setReviewerId(getCurrentUserId());
        task.setReviewTime(LocalDateTime.now());
        taskRepository.save(task);
        
        // 执行处理操作
        if ("REJECT".equals(review.getResult())) {
            messageService撤回消息(task.getMessageId(), review.getReason());
        }
    }
}
```

### 审核任务分配

```java
@Service
public class ModerationTaskScheduler {
    
    @Autowired
    private ModerationTaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 自动分配任务
    @Scheduled(fixedDelay = 5000)
    public void assignTasks() {
        // 查找未分配的任务
        List<ModerationTask> unassignedTasks = 
            taskRepository.findUnassignedTasks();
        
        // 查找空闲的审核员
        List<User> availableReviewers = 
            userRepository.findAvailableReviewers();
        
        // 负载均衡分配
        Map<Long, Integer> workload = calculateWorkload(availableReviewers);
        
        for (ModerationTask task : unassignedTasks) {
            Long bestReviewer = findLeastLoadedReviewer(workload);
            task.assignTo(bestReviewer);
            workload.put(bestReviewer, workload.get(bestReviewer) + 1);
            taskRepository.save(task);
        }
    }
    
    // 优先级调度
    public void processByPriority() {
        List<ModerationTask> tasks = taskRepository.findAllPending();
        
        // 按优先级排序
        tasks.sort((a, b) -> {
            int priorityCompare = b.getPriority().getValue() 
                              - a.getPriority().getValue();
            if (priorityCompare != 0) return priorityCompare;
            
            return a.getCreateTime().compareTo(b.getCreateTime());
        });
        
        // 优先处理高优先级任务
        for (ModerationTask task : tasks) {
            assignToAvailableReviewer(task);
        }
    }
}
```

## 举报与反馈系统

```java
@Service
public class ReportService {
    
    public ReportResult handleReport(Long userId, ReportRequest request) {
        // 1. 基础校验
        if (isRateLimited(userId)) {
            return ReportResult.rateLimited();
        }
        
        // 2. 重复举报检查
        if (hasRecentReport(userId, request.getTargetId())) {
            return ReportResult.alreadyReported();
        }
        
        // 3. 保存举报记录
        Report report = new Report();
        report.setReporterId(userId);
        report.setTargetId(request.getTargetId());
        report.setTargetType(request.getTargetType());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setEvidence(request.getEvidence());
        reportRepository.save(report);
        
        // 4. 触发自动审核
        autoModerationService.review(report);
        
        // 5. 记录用户举报次数
        userReportStats.incrementReportCount(userId);
        
        return ReportResult.success();
    }
}
```

## 最佳实践

### 1. 分层防御策略

```
第一层：敏感词过滤（<1ms）
    ↓
第二层：频率限制（<1ms）
    ↓
第三层：规则引擎（<5ms）
    ↓
第四层：AI模型（<100ms）
    ↓
第五层：人工复核（异步）
```

### 2. 性能优化

```java
// 敏感词过滤缓存
@Cacheable(value = "sensitiveWords", key = "#text.hashCode()")
public SensitiveWordCheckResult checkSensitiveWords(String text) {
    // DFA匹配
}

// 热点内容缓存
@Cacheable(value = "hotContent", key = "#contentId", 
           cacheManager = "redisCacheManager")
public ContentModerationResult getCachedResult(Long contentId) {
    // 返回缓存结果
}
```

### 3. 监控与告警

```yaml
# Prometheus 监控指标
moderation_metrics:
  - name: moderation_requests_total
    type: counter
    labels: [type, result]
  - name: moderation_latency_seconds
    type: histogram
    labels: [type]
  - name: moderation_violations_total
    type: counter
    labels: [category, level]
```

### 4. 灰度发布

```java
@Configuration
public class ModerationStrategyConfig {
    
    @Bean
    public ModerationStrategy moderationStrategy(
            @Value("${moderation.rollout.percentage:10}") int percentage) {
        if (percentage >= 100) {
            return new AIModerationStrategy();
        } else if (percentage > 0) {
            return new HybridModerationStrategy(percentage);
        } else {
            return new RuleBasedModerationStrategy();
        }
    }
}
```

## 总结

消息内容审核与反垃圾系统是IM平台安全运营的核心保障。一个完善的系统需要：

1. **多层防御**：从敏感词过滤到AI模型，形成立体防护
2. **实时性**：关键路径<10ms，保证用户体验
3. **准确性**：平衡误杀率和漏过率
4. **可扩展性**：支持海量数据和高并发
5. **人机协同**：AI初筛 + 人工复核的混合模式
6. **持续学习**：基于反馈数据不断优化模型

## 参考资料

- 阿里云内容安全
- 腾讯云安全审核
- 网易云盾
- AWS Rekognition
- Google Cloud Vision API
- Azure Content Moderator
