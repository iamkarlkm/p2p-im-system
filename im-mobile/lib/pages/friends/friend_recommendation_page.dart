import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:im_mobile/models/recommended_user_model.dart';
import 'package:im_mobile/services/friend_recommendation_service.dart';
import 'package:im_mobile/stores/user_store.dart';
import 'package:im_mobile/utils/toast_util.dart';
import 'package:im_mobile/widgets/avatar_widget.dart';
import 'package:im_mobile/widgets/loading_widget.dart';
import 'package:im_mobile/widgets/empty_widget.dart';

class FriendRecommendationPage extends StatefulWidget {
  const FriendRecommendationPage({Key? key}) : super(key: key);

  @override
  State<FriendRecommendationPage> createState() => _FriendRecommendationPageState();
}

class _FriendRecommendationPageState extends State<FriendRecommendationPage>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final FriendRecommendationService _service = FriendRecommendationService();
  final UserStore _userStore = UserStore();
  
  List<RecommendedUserModel> _recommendations = [];
  bool _isLoading = true;
  String _currentAlgorithm = 'mixed';
  final Set<String> _ignoredIds = {};

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _tabController.addListener(_onTabChanged);
    _loadRecommendations();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  void _onTabChanged() {
    if (!_tabController.indexIsChanging) {
      final algorithms = ['mixed', 'mutual_friends', 'interest_tags', 'group_relation'];
      setState(() {
        _currentAlgorithm = algorithms[_tabController.index];
      });
      _loadRecommendations();
    }
  }

  Future<void> _loadRecommendations() async {
    setState(() => _isLoading = true);
    try {
      final result = await _service.getRecommendations(
        page: 1,
        size: 20,
        algorithm: _currentAlgorithm == 'mixed' ? null : _currentAlgorithm,
      );
      setState(() {
        _recommendations = result;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      ToastUtil.showError('获取推荐列表失败');
    }
  }

  Future<void> _addFriend(String userId) async {
    try {
      await _service.sendFriendRequest(userId);
      ToastUtil.showSuccess('好友请求已发送');
      setState(() {
        _recommendations.removeWhere((r) => r.userId == userId);
      });
    } catch (e) {
      ToastUtil.showError('发送请求失败');
    }
  }

  Future<void> _ignoreRecommendation(String userId) async {
    try {
      await _service.ignoreRecommendation(userId);
      setState(() {
        _ignoredIds.add(userId);
        _recommendations.removeWhere((r) => r.userId == userId);
      });
      ToastUtil.showSuccess('已忽略此推荐');
    } catch (e) {
      ToastUtil.showError('操作失败');
    }
  }

  IconData _getReasonIcon(String reasonType) {
    switch (reasonType) {
      case 'mutual_friends':
        return Icons.people_outline;
      case 'interest_tags':
        return Icons.local_offer_outlined;
      case 'group_relation':
        return Icons.group_outlined;
      default:
        return Icons.person_add_outlined;
    }
  }

  Color _getReasonColor(String reasonType) {
    switch (reasonType) {
      case 'mutual_friends':
        return Colors.blue;
      case 'interest_tags':
        return Colors.green;
      case 'group_relation':
        return Colors.purple;
      default:
        return Colors.orange;
    }
  }

  String _getTabLabel(int index) {
    const labels = ['全部', '共同好友', '兴趣', '群组'];
    return labels[index];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.white,
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '好友推荐',
              style: TextStyle(
                color: Colors.black87,
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            Text(
              '根据您的社交网络为您推荐',
              style: TextStyle(
                color: Colors.black54,
                fontSize: 12,
              ),
            ),
          ],
        ),
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          labelColor: Theme.of(context).primaryColor,
          unselectedLabelColor: Colors.black54,
          indicatorColor: Theme.of(context).primaryColor,
          tabs: List.generate(4, (index) => Tab(text: _getTabLabel(index))),
        ),
      ),
      body: _isLoading
          ? const LoadingWidget(message: '正在为您寻找好友...')
          : _recommendations.isEmpty
              ? const EmptyWidget(
                  icon: Icons.person_off_outlined,
                  title: '暂无推荐好友',
                  subtitle: '我们会持续为您寻找可能认识的人',
                )
              : RefreshIndicator(
                  onRefresh: _loadRecommendations,
                  child: ListView.builder(
                    padding: const EdgeInsets.all(12),
                    itemCount: _recommendations.length,
                    itemBuilder: (context, index) {
                      return _buildRecommendationCard(_recommendations[index]);
                    },
                  ),
                ),
    );
  }

  Widget _buildRecommendationCard(RecommendedUserModel user) {
    return Dismissible(
      key: Key(user.userId),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 20),
        decoration: BoxDecoration(
          color: Colors.red.shade100,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.delete_outline, color: Colors.red),
            SizedBox(height: 4),
            Text('忽略', style: TextStyle(color: Colors.red, fontSize: 12)),
          ],
        ),
      ),
      onDismissed: (_) => _ignoreRecommendation(user.userId),
      child: Card(
        margin: const EdgeInsets.only(bottom: 12),
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  AvatarWidget(
                    url: user.avatar,
                    size: 56,
                    name: user.nickname,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          user.nickname,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: 6),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: _getReasonColor(user.reasonType).withOpacity(0.1),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(
                                _getReasonIcon(user.reasonType),
                                size: 14,
                                color: _getReasonColor(user.reasonType),
                              ),
                              const SizedBox(width: 4),
                              Text(
                                user.reasonDescription,
                                style: TextStyle(
                                  fontSize: 12,
                                  color: _getReasonColor(user.reasonType),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              if (user.mutualFriendsCount != null && user.mutualFriendsCount! > 0)
                Padding(
                  padding: const EdgeInsets.only(top: 12),
                  child: Row(
                    children: [
                      const Icon(Icons.people_outline, size: 16, color: Colors.grey),
                      const SizedBox(width: 6),
                      Text(
                        '${user.mutualFriendsCount} 个共同好友',
                        style: const TextStyle(fontSize: 13, color: Colors.grey),
                      ),
                    ],
                  ),
                ),
              if (user.commonTags != null && user.commonTags!.isNotEmpty)
                Padding(
                  padding: const EdgeInsets.only(top: 8),
                  child: Row(
                    children: [
                      const Icon(Icons.local_offer_outlined, size: 16, color: Colors.grey),
                      const SizedBox(width: 6),
                      Expanded(
                        child: Text(
                          '共同兴趣: ${user.commonTags!.take(3).join(', ')}',
                          style: const TextStyle(fontSize: 13, color: Colors.grey),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                ),
              if (user.commonGroups != null && user.commonGroups!.isNotEmpty)
                Padding(
                  padding: const EdgeInsets.only(top: 8),
                  child: Row(
                    children: [
                      const Icon(Icons.group_outlined, size: 16, color: Colors.grey),
                      const SizedBox(width: 6),
                      Expanded(
                        child: Text(
                          '同群组: ${user.commonGroups!.take(2).join(', ')}',
                          style: const TextStyle(fontSize: 13, color: Colors.grey),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                ),
              Padding(
                padding: const EdgeInsets.only(top: 12),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                      decoration: BoxDecoration(
                        color: Theme.of(context).primaryColor.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        '匹配度 ${(user.score * 100).toInt()}%',
                        style: TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                          color: Theme.of(context).primaryColor,
                        ),
                      ),
                    ),
                    Row(
                      children: [
                        OutlinedButton.icon(
                          onPressed: () => _ignoreRecommendation(user.userId),
                          icon: const Icon(Icons.close, size: 18),
                          label: const Text('忽略'),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: Colors.grey,
                            padding: const EdgeInsets.symmetric(horizontal: 12),
                          ),
                        ),
                        const SizedBox(width: 8),
                        ElevatedButton.icon(
                          onPressed: () => _addFriend(user.userId),
                          icon: const Icon(Icons.person_add, size: 18),
                          label: const Text('添加'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Theme.of(context).primaryColor,
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
