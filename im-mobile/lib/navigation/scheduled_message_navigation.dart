import 'package:flutter/material.dart';
import 'package:im_mobile/pages/scheduled_message_page.dart';

class ScheduledMessageNavigation {
  static Widget buildNavItem(BuildContext context, {bool selected = false}) {
    return ListTile(
      leading: const Icon(Icons.schedule),
      title: const Text('定时消息'),
      selected: selected,
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const ScheduledMessagePage()),
        );
      },
    );
  }
}
