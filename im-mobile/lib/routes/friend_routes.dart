import 'package:flutter/material.dart';
import 'package:im_mobile/pages/friends/friend_recommendation_page.dart';

class FriendRoutes {
  static const String recommendations = '/friends/recommendations';

  static Map<String, WidgetBuilder> get routes => {
    recommendations: (context) => const FriendRecommendationPage(),
  };

  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    switch (settings.name) {
      case recommendations:
        return MaterialPageRoute(
          builder: (context) => const FriendRecommendationPage(),
          settings: settings,
        );
      default:
        return MaterialPageRoute(
          builder: (context) => const Scaffold(
            body: Center(child: Text('Page not found')),
          ),
        );
    }
  }
}
