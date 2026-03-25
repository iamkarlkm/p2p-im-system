// Reply Chain Screen for im-mobile

import 'package:flutter/material.dart';
import '../models/message_reply_chain.dart';
import '../services/reply_chain_service.dart';

class ReplyChainScreen extends StatefulWidget {
  final int conversationId;
  final String userId;
  final String nickname;

  ReplyChainScreen({
    required this.conversationId,
    required this.userId,
    required this.nickname,
  });

  @override
  _ReplyChainScreenState createState() => _ReplyChainScreenState();
}

class _ReplyChainScreenState extends State<ReplyChainScreen> {
  late ReplyChainService _service;
  List<MessageReplyChain> _chains = [];
  MessageReplyChain? _selectedChain;
  bool _loading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _service = ReplyChainService(userId: widget.userId, nickname: widget.nickname);
    _loadChains();
  }

  Future<void> _loadChains() async {
    setState(() { _loading = true; _error = null; });
    try {
      final chains = await _service.getConversationReplyChains(widget.conversationId);
      setState(() { _chains = chains; _loading = false; });
    } catch (e) {
      setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _viewBranch(MessageReplyChain chain) async {
    setState(() { _loading = true; });
    try {
      final branch = await _service.getBranchTree(chain.rootMessageId);
      setState(() { _selectedChain = branch; _loading = false; });
    } catch (e) {
      setState(() { _error = e.toString(); _loading = false; });
    }
  }

  void _closeBranch() {
    setState(() { _selectedChain = null; });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('消息回复链'),
        actions: [
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: _loadChains,
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_loading) return Center(child: CircularProgressIndicator());
    if (_error != null) return Center(child: Text('Error: $_error', style: TextStyle(color: Colors.red)));
    if (_selectedChain != null) return _buildBranchView(_selectedChain!);
    return _buildChainList();
  }

  Widget _buildChainList() {
    if (_chains.isEmpty) {
      return Center(child: Text('暂无回复链', style: TextStyle(color: Colors.grey)));
    }
    return RefreshIndicator(
      onRefresh: _loadChains,
      child: ListView.builder(
        itemCount: _chains.length,
        itemBuilder: (ctx, idx) {
          final chain = _chains[idx];
          return Card(
            margin: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.blue,
                child: Text('${chain.depth}', style: TextStyle(color: Colors.white, fontSize: 14)),
              ),
              title: Text(chain.userNickname),
              subtitle: Text(
                '深度 ${chain.depth} · ${chain.branchNodes.length} 条回复',
                style: TextStyle(fontSize: 12),
              ),
              trailing: Icon(Icons.chevron_right),
              onTap: () => _viewBranch(chain),
            ),
          );
        },
      ),
    );
  }

  Widget _buildBranchView(MessageReplyChain chain) {
    return Column(
      children: [
        Container(
          padding: EdgeInsets.all(12),
          color: Colors.blue.shade50,
          child: Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('回复链详情', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    SizedBox(height: 4),
                    Text('深度 ${chain.depth} · ${chain.branchNodes.length} 个节点',
                        style: TextStyle(color: Colors.grey, fontSize: 13)),
                  ],
                ),
              ),
              TextButton(onPressed: _closeBranch, child: Text('返回')),
            ],
          ),
        ),
        Expanded(
          child: ListView(
            padding: EdgeInsets.all(12),
            children: [
              _buildChainNode(chain, 0),
              ...chain.branchNodes.asMap().entries.map((e) {
                return _buildNodeCard(e.value, e.key + 1);
              }),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChainNode(MessageReplyChain chain, int indent) {
    return Container(
      margin: EdgeInsets.only(left: indent * 16.0, bottom: 8),
      padding: EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border.all(color: Colors.blue.shade200),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(chain.userNickname, style: TextStyle(fontWeight: FontWeight.bold)),
          SizedBox(height: 4),
          Text('深度: ${chain.depth}', style: TextStyle(color: Colors.grey, fontSize: 12)),
          if (chain.context != null) ...[
            SizedBox(height: 4),
            Text('"${chain.context!.content}"',
                style: TextStyle(fontStyle: FontStyle.italic, color: Colors.grey)),
          ],
        ],
      ),
    );
  }

  Widget _buildNodeCard(ReplyChainNode node, int index) {
    return Container(
      margin: EdgeInsets.only(left: (index + 1) * 16.0, bottom: 8),
      padding: EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey.shade50,
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(radius: 12, backgroundColor: Colors.green, child: Icon(Icons.reply, size: 14, color: Colors.white)),
              SizedBox(width: 8),
              Expanded(child: Text(node.userNickname, style: TextStyle(fontWeight: FontWeight.bold))),
            ],
          ),
          SizedBox(height: 8),
          if (node.contentPreview.isNotEmpty)
            Text(node.contentPreview, style: TextStyle(fontSize: 13)),
          SizedBox(height: 4),
          Text(
            '${node.messageType} · 位置 $index',
            style: TextStyle(color: Colors.grey, fontSize: 11),
          ),
        ],
      ),
    );
  }
}
