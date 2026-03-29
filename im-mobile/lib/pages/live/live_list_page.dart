import 'package:flutter/material.dart';
import '../../models/live/live_room_model.dart';
import '../../services/live/mini_program_live_service.dart';
import '../../services/live/live_appointment_service.dart';

/// 直播列表页面
class LiveListPage extends StatefulWidget {
  const LiveListPage({Key? key}) : super(key: key);

  @override
  State<LiveListPage> createState() => _LiveListPageState();
}

class _LiveListPageState extends State<LiveListPage>
    with SingleTickerProviderStateMixin {
  final MiniProgramLiveService _liveService = MiniProgramLiveService();
  final LiveAppointmentService _appointmentService = LiveAppointmentService();
  
  late TabController _tabController;
  
  List<LiveRoomModel> _liveRooms = [];
  List<LiveRoomModel> _recommendedRooms = [];
  List<LiveAppointmentModel> _myAppointments = [];
  List<LiveReplay> _replays = [];
  
  bool _isLoading = false;
  int _currentPage = 1;
  final int _pageSize = 20;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    
    await Future.wait([
      _loadLiveRooms(),
      _loadRecommendedRooms(),
      _loadMyAppointments(),
      _loadReplays(),
    ]);
    
    setState(() => _isLoading = false);
  }

  Future<void> _loadLiveRooms() async {
    try {
      final rooms = await _liveService.getLiveRoomList(
        page: _currentPage,
        pageSize: _pageSize,
      );
      setState(() {
        _liveRooms = rooms;
      });
    } catch (e) {
      // 错误处理
    }
  }

  Future<void> _loadRecommendedRooms() async {
    try {
      final rooms = await _liveService.getRecommendedRooms();
      setState(() {
        _recommendedRooms = rooms;
      });
    } catch (e) {
      // 错误处理
    }
  }

  Future<void> _loadMyAppointments() async {
    await _appointmentService.loadAppointments();
    setState(() {
      _myAppointments = _appointmentService.appointments;
    });
  }

  Future<void> _loadReplays() async {
    // TODO: 加载回放列表
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('直播'),
        elevation: 0,
        bottom: TabBar(
          controller: _tabController,
          tabs: [
            Tab(text: '直播中'),
            Tab(text: '推荐'),
            Tab(text: '预约'),
            Tab(text: '回放'),
          ],
          indicatorColor: Colors.pink,
          labelColor: Colors.pink,
          unselectedLabelColor: Colors.grey,
        ),
        actions: [
          IconButton(
            icon: Icon(Icons.search),
            onPressed: () {
              // TODO: 搜索
            },
          ),
        ],
      ),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : TabBarView(
              controller: _tabController,
              children: [
                _buildLiveRoomsTab(),
                _buildRecommendedTab(),
                _buildAppointmentsTab(),
                _buildReplaysTab(),
              ],
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          Navigator.pushNamed(context, '/live/streamer');
        },
        icon: Icon(Icons.videocam),
        label: Text('我要直播'),
        backgroundColor: Colors.pink,
      ),
    );
  }

  /// 构建直播中标签页
  Widget _buildLiveRoomsTab() {
    if (_liveRooms.isEmpty) {
      return _buildEmptyView('暂无正在直播的房间');
    }

    return RefreshIndicator(
      onRefresh: _loadLiveRooms,
      child: GridView.builder(
        padding: EdgeInsets.all(12),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.75,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
        ),
        itemCount: _liveRooms.length,
        itemBuilder: (context, index) {
          return _buildLiveRoomCard(_liveRooms[index]);
        },
      ),
    );
  }

  /// 构建推荐标签页
  Widget _buildRecommendedTab() {
    if (_recommendedRooms.isEmpty) {
      return _buildEmptyView('暂无推荐直播');
    }

    return ListView.builder(
      padding: EdgeInsets.all(12),
      itemCount: _recommendedRooms.length,
      itemBuilder: (context, index) {
        return _buildLiveRoomListItem(_recommendedRooms[index]);
      },
    );
  }

  /// 构建预约标签页
  Widget _buildAppointmentsTab() {
    final upcoming = _appointmentService.upcomingAppointments;
    final history = _appointmentService.historyAppointments;

    if (upcoming.isEmpty && history.isEmpty) {
      return _buildEmptyView('暂无直播预约');
    }

    return ListView(
      padding: EdgeInsets.all(12),
      children: [
        if (upcoming.isNotEmpty) ...[
          Padding(
            padding: EdgeInsets.symmetric(vertical: 8),
            child: Text(
              '即将开播',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
          ),
          ...upcoming.map((appointment) => _buildAppointmentCard(appointment)),
        ],
        if (history.isNotEmpty) ...[
          Padding(
            padding: EdgeInsets.symmetric(vertical: 8),
            child: Text(
              '历史预约',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
          ),
          ...history.map((appointment) => _buildAppointmentCard(appointment, isHistory: true)),
        ],
      ],
    );
  }

  /// 构建回放标签页
  Widget _buildReplaysTab() {
    if (_replays.isEmpty) {
      return _buildEmptyView('暂无直播回放');
    }

    return ListView.builder(
      padding: EdgeInsets.all(12),
      itemCount: _replays.length,
      itemBuilder: (context, index) {
        return _buildReplayCard(_replays[index]);
      },
    );
  }

  /// 构建空视图
  Widget _buildEmptyView(String message) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.live_tv, size: 64, color: Colors.grey[400]),
          SizedBox(height: 16),
          Text(message, style: TextStyle(color: Colors.grey[600])),
        ],
      ),
    );
  }

  /// 构建直播间卡片
  Widget _buildLiveRoomCard(LiveRoomModel room) {
    return GestureDetector(
      onTap: () => _enterLiveRoom(room),
      child: Card(
        clipBehavior: Clip.antiAlias,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        child: Stack(
          fit: StackFit.expand,
          children: [
            // 封面图
            room.coverImage != null
                ? Image.network(
                    room.coverImage!,
                    fit: BoxFit.cover,
                  )
                : Container(
                    color: Colors.grey[300],
                    child: Icon(Icons.live_tv, size: 48, color: Colors.grey),
                  ),

            // 渐变遮罩
            Positioned(
              bottom: 0,
              left: 0,
              right: 0,
              child: Container(
                padding: EdgeInsets.all(12),
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.bottomCenter,
                    end: Alignment.topCenter,
                    colors: [Colors.black87, Colors.transparent],
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      room.title,
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(Icons.person, color: Colors.white70, size: 14),
                        SizedBox(width: 4),
                        Text(
                          room.streamerName,
                          style: TextStyle(color: Colors.white70, fontSize: 12),
                        ),
                        Spacer(),
                        Icon(Icons.visibility, color: Colors.white70, size: 14),
                        SizedBox(width: 4),
                        Text(
                          '${room.onlineCount}',
                          style: TextStyle(color: Colors.white70, fontSize: 12),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            // 直播中标签
            Positioned(
              top: 8,
              left: 8,
              child: Container(
                padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.red,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 6,
                      height: 6,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        shape: BoxShape.circle,
                      ),
                    ),
                    SizedBox(width: 4),
                    Text(
                      '直播中',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// 构建直播间列表项
  Widget _buildLiveRoomListItem(LiveRoomModel room) {
    return Card(
      margin: EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: EdgeInsets.all(12),
        leading: ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: room.coverImage != null
              ? Image.network(
                  room.coverImage!,
                  width: 80,
                  height: 80,
                  fit: BoxFit.cover,
                )
              : Container(
                  width: 80,
                  height: 80,
                  color: Colors.grey[300],
                  child: Icon(Icons.live_tv),
                ),
        ),
        title: Text(room.title),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(height: 4),
            Text(room.streamerName),
            SizedBox(height: 4),
            Row(
              children: [
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    '直播中',
                    style: TextStyle(color: Colors.white, fontSize: 10),
                  ),
                ),
                SizedBox(width: 8),
                Icon(Icons.visibility, size: 14, color: Colors.grey),
                SizedBox(width: 4),
                Text(
                  '${room.onlineCount}',
                  style: TextStyle(color: Colors.grey, fontSize: 12),
                ),
              ],
            ),
          ],
        ),
        onTap: () => _enterLiveRoom(room),
      ),
    );
  }

  /// 构建预约卡片
  Widget _buildAppointmentCard(LiveAppointmentModel appointment, {bool isHistory = false}) {
    return Card(
      margin: EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: EdgeInsets.all(12),
        leading: ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: appointment.coverImage != null
              ? Image.network(
                  appointment.coverImage!,
                  width: 80,
                  height: 80,
                  fit: BoxFit.cover,
                )
              : Container(
                  width: 80,
                  height: 80,
                  color: Colors.grey[300],
                  child: Icon(Icons.live_tv),
                ),
        ),
        title: Text(appointment.title),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(height: 4),
            if (appointment.streamerName != null)
              Text(appointment.streamerName!),
            SizedBox(height: 4),
            Text(
              appointment.countdownText,
              style: TextStyle(
                color: isHistory ? Colors.grey : Colors.pink,
                fontWeight: isHistory ? FontWeight.normal : FontWeight.bold,
              ),
            ),
          ],
        ),
        trailing: isHistory
            ? null
            : TextButton(
                onPressed: () => _appointmentService.cancelAppointment(appointment.id),
                child: Text('取消'),
              ),
      ),
    );
  }

  /// 构建回放卡片
  Widget _buildReplayCard(LiveReplay replay) {
    return Card(
      margin: EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: EdgeInsets.all(12),
        leading: ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: Stack(
            children: [
              replay.coverImage != null
                  ? Image.network(
                      replay.coverImage!,
                      width: 80,
                      height: 80,
                      fit: BoxFit.cover,
                    )
                  : Container(
                      width: 80,
                      height: 80,
                      color: Colors.grey[300],
                      child: Icon(Icons.videocam),
                    ),
              Positioned(
                bottom: 4,
                right: 4,
                child: Container(
                  padding: EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.black54,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    '${replay.duration.inMinutes}:${(replay.duration.inSeconds % 60).toString().padLeft(2, '0')}',
                    style: TextStyle(color: Colors.white, fontSize: 10),
                  ),
                ),
              ),
            ],
          ),
        ),
        title: Text(replay.title),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(height: 4),
            Row(
              children: [
                Icon(Icons.visibility, size: 14, color: Colors.grey),
                SizedBox(width: 4),
                Text(
                  '${replay.viewCount}',
                  style: TextStyle(color: Colors.grey, fontSize: 12),
                ),
                SizedBox(width: 16),
                Icon(Icons.favorite, size: 14, color: Colors.grey),
                SizedBox(width: 4),
                Text(
                  '${replay.likeCount}',
                  style: TextStyle(color: Colors.grey, fontSize: 12),
                ),
              ],
            ),
          ],
        ),
        onTap: () {
          // TODO: 播放回放
        },
      ),
    );
  }

  /// 进入直播间
  void _enterLiveRoom(LiveRoomModel room) {
    Navigator.pushNamed(
      context,
      '/live/room',
      arguments: {'roomId': room.id},
    );
  }
}
