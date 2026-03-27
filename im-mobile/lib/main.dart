import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'screens/login_screen.dart';
import 'screens/chat_screen.dart';
import 'screens/friends_screen.dart';
import 'screens/groups_screen.dart';
import 'screens/settings_screen.dart';
import 'screens/ai_assistant_screen.dart';
import 'screens/status_settings_screen.dart';
import 'screens/notification_settings_screen.dart';
import 'screens/message_search_screen.dart';
import 'services/auth_service.dart';
import 'services/websocket_service.dart';
import 'services/friend_service.dart';
import 'services/group_service.dart';
import 'services/ai_assistant_service.dart';
import 'services/user_status_service.dart';
import 'services/push_notification_service.dart';
import 'services/message_search_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthService()),
        ChangeNotifierProvider(create: (_) => WebSocketService()),
        ChangeNotifierProvider(create: (_) => FriendService()),
        ChangeNotifierProvider(create: (_) => GroupService()),
        ChangeNotifierProvider(create: (_) => AiAssistantService()),
        ChangeNotifierProvider(create: (_) => UserStatusService()),
        ChangeNotifierProvider(create: (_) => PushNotificationService()),
        ChangeNotifierProvider(create: (_) => MessageSearchService()),
      ],
      child: MaterialApp(
        title: 'IM Mobile',
        theme: ThemeData(
          primarySwatch: Colors.blue,
          useMaterial3: true,
        ),
        home: const AuthWrapper(),
        routes: {
          '/login': (context) => const LoginScreen(),
          '/chat': (context) => const ChatScreen(),
          '/friends': (context) => const FriendsScreen(),
          '/groups': (context) => const GroupsScreen(),
          '/settings': (context) => const SettingsScreen(),
          '/ai_assistant': (context) => const AiAssistantScreen(),
          '/status_settings': (context) => const StatusSettingsScreen(),
          '/notification_settings': (context) => const NotificationSettingsScreen(),
          '/message_search': (context) => const MessageSearchScreen(),
        },
      ),
    );
  }
}

class AuthWrapper extends StatefulWidget {
  const AuthWrapper({super.key});

  @override
  State<AuthWrapper> createState() => _AuthWrapperState();
}

class _AuthWrapperState extends State<AuthWrapper> {
  int _currentIndex = 0;
  
  final List<Widget> _pages = [
    const ChatScreen(),
    const FriendsScreen(),
    const GroupsScreen(),
    const AiAssistantScreen(),
    const SettingsScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    
    if (!authService.isLoggedIn) {
      return const LoginScreen();
    }
    
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _pages,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.chat_outlined),
            selectedIcon: Icon(Icons.chat),
            label: '消息',
          ),
          NavigationDestination(
            icon: Icon(Icons.people_outline),
            selectedIcon: Icon(Icons.people),
            label: '好友',
          ),
          NavigationDestination(
            icon: Icon(Icons.group_outlined),
            selectedIcon: Icon(Icons.group),
            label: '群组',
          ),
          NavigationDestination(
            icon: Icon(Icons.smart_toy_outlined),
            selectedIcon: Icon(Icons.smart_toy),
            label: 'AI助手',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings),
            label: '设置',
          ),
        ],
      ),
    );
  }
}
