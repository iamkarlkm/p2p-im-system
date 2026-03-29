// models/do_not_disturb_period_model.dart
import 'package:json_annotation/json_annotation.dart';

part 'do_not_disturb_period_model.g.dart';

@JsonSerializable()
class DoNotDisturbPeriodModel {
  final String id;
  final String userId;
  final String name;
  final int startHour;
  final int startMinute;
  final int endHour;
  final int endMinute;
  final List<int> activeDays;
  final bool isEnabled;
  final bool allowCalls;
  final bool allowMentions;
  final String? createdAt;
  final String? updatedAt;

  DoNotDisturbPeriodModel({
    required this.id,
    required this.userId,
    required this.name,
    required this.startHour,
    required this.startMinute,
    required this.endHour,
    required this.endMinute,
    required this.activeDays,
    this.isEnabled = true,
    this.allowCalls = false,
    this.allowMentions = true,
    this.createdAt,
    this.updatedAt,
  });

  factory DoNotDisturbPeriodModel.fromJson(Map<String, dynamic> json) =>
      _$DoNotDisturbPeriodModelFromJson(json);

  Map<String, dynamic> toJson() => _$DoNotDisturbPeriodModelToJson(this);

  DoNotDisturbPeriodModel copyWith({
    String? id,
    String? userId,
    String? name,
    int? startHour,
    int? startMinute,
    int? endHour,
    int? endMinute,
    List<int>? activeDays,
    bool? isEnabled,
    bool? allowCalls,
    bool? allowMentions,
    String? createdAt,
    String? updatedAt,
  }) {
    return DoNotDisturbPeriodModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      name: name ?? this.name,
      startHour: startHour ?? this.startHour,
      startMinute: startMinute ?? this.startMinute,
      endHour: endHour ?? this.endHour,
      endMinute: endMinute ?? this.endMinute,
      activeDays: activeDays ?? this.activeDays,
      isEnabled: isEnabled ?? this.isEnabled,
      allowCalls: allowCalls ?? this.allowCalls,
      allowMentions: allowMentions ?? this.allowMentions,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  String get formattedTimeRange {
    String formatTime(int hour, int minute) {
      return '${hour.toString().padLeft(2, '0')}:${minute.toString().padLeft(2, '0')}';
    }
    return '${formatTime(startHour, startMinute)} - ${formatTime(endHour, endMinute)}';
  }

  String get activeDaysText {
    if (activeDays.length == 7) return '每天';
    if (activeDays.length == 5 && 
        activeDays.every((d) => d >= 1 && d <= 5)) return '工作日';
    if (activeDays.length == 2 && 
        activeDays.every((d) => d == 6 || d == 7)) return '周末';
    
    const dayNames = ['日', '一', '二', '三', '四', '五', '六'];
    return activeDays.map((d) => '周${dayNames[d % 7]}').join('、');
  }

  bool isCurrentlyActive() {
    if (!isEnabled) return false;
    
    final now = DateTime.now();
    final currentDay = now.weekday;
    
    if (!activeDays.contains(currentDay)) return false;
    
    final currentMinutes = now.hour * 60 + now.minute;
    final startMinutes = startHour * 60 + startMinute;
    final endMinutes = endHour * 60 + endMinute;
    
    if (startMinutes <= endMinutes) {
      return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
    } else {
      return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
    }
  }
}
