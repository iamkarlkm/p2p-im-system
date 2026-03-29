package com.im.backend.enums;

/**
 * POI分类枚举
 * 定义兴趣点的分类体系
 * 
 * @author IM Development Team
 * @version 1.0
 */
public enum PoiCategory {
    
    // 餐饮美食
    FOOD("1000", "餐饮美食", 1, null),
    RESTAURANT("1100", "中餐厅", 2, "1000"),
    WESTERN_FOOD("1200", "西餐厅", 2, "1000"),
    FAST_FOOD("1300", "快餐", 2, "1000"),
    CAFE("1400", "咖啡厅", 2, "1000"),
    DESSERT("1500", "甜品饮品", 2, "1000"),
    SNACK("1600", "小吃", 2, "1000"),
    BBQ("1700", "烧烤", 2, "1000"),
    HOT_POT("1800", "火锅", 2, "1000"),
    
    // 购物消费
    SHOPPING("2000", "购物消费", 1, null),
    MALL("2100", "购物中心", 2, "2000"),
    SUPERMARKET("2200", "超市", 2, "2000"),
    CONVENIENCE("2300", "便利店", 2, "2000"),
    CLOTHING("2400", "服装鞋帽", 2, "2000"),
    DIGITAL("2500", "数码家电", 2, "2000"),
    COSMETICS("2600", "美妆个护", 2, "2000"),
    BOOKSTORE("2700", "书店", 2, "2000"),
    
    // 生活服务
    SERVICE("3000", "生活服务", 1, null),
    BANK("3100", "银行", 2, "3000"),
    ATM("3200", "ATM", 2, "3000"),
    POST("3300", "邮局", 2, "3000"),
    LAUNDRY("3400", "洗衣店", 2, "3000"),
    HAIR_SALON("3500", "理发店", 2, "3000"),
    GAS_STATION("3600", "加油站", 2, "3000"),
    PARKING("3700", "停车场", 2, "3000"),
    
    // 休闲娱乐
    ENTERTAINMENT("4000", "休闲娱乐", 1, null),
    KTV("4100", "KTV", 2, "4000"),
    CINEMA("4200", "电影院", 2, "4000"),
    BAR("4300", "酒吧", 2, "4000"),
    GAME_CENTER("4400", "游戏厅", 2, "4000"),
    BOWLING("4500", "保龄球", 2, "4000"),
    GYM("4600", "健身房", 2, "4000"),
    PARK("4700", "公园", 2, "4000"),
    
    // 酒店住宿
    HOTEL("5000", "酒店住宿", 1, null),
    STAR_HOTEL("5100", "星级酒店", 2, "5000"),
    BUDGET_HOTEL("5200", "经济型酒店", 2, "5000"),
    INN("5300", "客栈民宿", 2, "5000"),
    HOSTEL("5400", "青年旅舍", 2, "5000"),
    
    // 旅游观光
    TOURISM("6000", "旅游观光", 1, null),
    SCENIC_SPOT("6100", "景点", 2, "6000"),
    MUSEUM("6200", "博物馆", 2, "6000"),
    TEMPLE("6300", "寺庙", 2, "6000"),
    BEACH("6400", "海滩", 2, "6000"),
    MOUNTAIN("6500", "山峰", 2, "6000"),
    
    // 交通出行
    TRANSPORT("7000", "交通出行", 1, null),
    AIRPORT("7100", "机场", 2, "7000"),
    TRAIN_STATION("7200", "火车站", 2, "7000"),
    BUS_STATION("7300", "汽车站", 2, "7000"),
    SUBWAY("7400", "地铁站", 2, "7000"),
    TAXI_STAND("7500", "出租车", 2, "7000"),
    
    // 医疗健康
    MEDICAL("8000", "医疗健康", 1, null),
    HOSPITAL("8100", "综合医院", 2, "8000"),
    CLINIC("8200", "诊所", 2, "8000"),
    PHARMACY("8300", "药店", 2, "8000"),
    DENTAL("8400", "齿科", 2, "8000"),
    
    // 教育培训
    EDUCATION("9000", "教育培训", 1, null),
    UNIVERSITY("9100", "大学", 2, "9000"),
    SCHOOL("9200", "中小学", 2, "9000"),
    TRAINING("9300", "培训机构", 2, "9000"),
    LIBRARY("9400", "图书馆", 2, "9000"),
    
    // 其他
    OTHER("0000", "其他", 1, null);
    
    /**
     * 分类编码
     */
    private final String code;
    
    /**
     * 分类名称
     */
    private final String name;
    
    /**
     * 层级
     */
    private final int level;
    
    /**
     * 父级编码
     */
    private final String parentCode;
    
    PoiCategory(String code, String name, int level, String parentCode) {
        this.code = code;
        this.name = name;
        this.level = level;
        this.parentCode = parentCode;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getParentCode() {
        return parentCode;
    }
    
    /**
     * 根据编码获取枚举
     */
    public static PoiCategory getByCode(String code) {
        for (PoiCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return OTHER;
    }
    
    /**
     * 根据名称获取枚举
     */
    public static PoiCategory getByName(String name) {
        for (PoiCategory category : values()) {
            if (category.name.equals(name)) {
                return category;
            }
        }
        return OTHER;
    }
    
    /**
     * 获取所有一级分类
     */
    public static PoiCategory[] getTopCategories() {
        return java.util.Arrays.stream(values())
                .filter(c -> c.level == 1)
                .toArray(PoiCategory[]::new);
    }
    
    /**
     * 获取子分类
     */
    public static PoiCategory[] getSubCategories(String parentCode) {
        return java.util.Arrays.stream(values())
                .filter(c -> parentCode.equals(c.parentCode))
                .toArray(PoiCategory[]::new);
    }
    
    /**
     * 是否为顶级分类
     */
    public boolean isTopCategory() {
        return level == 1;
    }
}
