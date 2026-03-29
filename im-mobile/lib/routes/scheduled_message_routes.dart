import 'package:flutter/material.dart';
import 'package:im_mobile/pages/scheduled_message_page.dart';

class ScheduledMessageRoutes {
  static const String scheduledMessages = '/scheduled-messages';

  static Map<String, WidgetBuilder> getRoutes() {
    return {
      scheduledMessages: (context) => const ScheduledMessagePage(),
    };
  }
}
