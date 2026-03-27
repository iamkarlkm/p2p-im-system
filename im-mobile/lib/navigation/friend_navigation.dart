import 'package:flutter/material.dart';
import 'package:im_mobile/pages/friends/friend_recommendation_page.dart';

class FriendNavigationItems {
  static const String recommendations = '/friends/recommendations';

  static BottomNavigationBarItem get recommendationsItem =>
      const BottomNavigationBarItem(
        icon: Icon(Icons.person_add_outlined),
        activeIcon: Icon(Icons.person_add),
        label: '推荐',
      );

  static NavigationRailDestination get recommendationsDestination =>
      const NavigationRailDestination(
        icon: Icon(Icons.person_add_outlined),
        selectedIcon: Icon(Icons.person_add),
        label: Text('推荐'),
      );

  static Widget get recommendationsPage => const FriendRecommendationPage();
}
