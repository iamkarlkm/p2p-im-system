/**
 * 用户资料页面
 */

import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:im_mobile/models/user_profile.dart';
import 'package:im_mobile/services/user_profile_service.dart';
import 'package:im_mobile/services/auth_service.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  final _service = UserProfileService();
  UserProfile? _profile;
  List<FriendGroup> _groups = [];
  bool _isLoading = true;
  bool _isSaving = false;

  // Form controllers
  final _nicknameController = TextEditingController();
  final _bioController = TextEditingController();
  final _emailController = TextEditingController();
  final _countryController = TextEditingController();
  final _cityController = TextEditingController();
  final _statusTextController = TextEditingController();
  int _gender = 0;
  DateTime? _birthday;
  OnlineStatus _onlineStatus = OnlineStatus.online;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    setState(() => _isLoading = true);
    try {
      final profile = await _service.getMyProfile();
      final groups = await _service.getFriendGroups();
      setState(() {
        _profile = profile;
        _groups = groups;
        _nicknameController.text = profile.nickname;
        _bioController.text = profile.bio;
        _emailController.text = profile.email ?? '';
        _countryController.text = profile.country ?? '';
        _cityController.text = profile.city ?? '';
        _statusTextController.text = profile.statusText ?? '';
        _gender = profile.gender;
        _birthday = profile.birthday;
        _onlineStatus = profile.onlineStatus;
      });
    } catch (e) {
      _showError('加载资料失败: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _saveProfile() async {
    setState(() => _isSaving = true);
    try {
      final updates = {
        'nickname': _nicknameController.text,
        'bio': _bioController.text,
        'gender': _gender,
        'email': _emailController.text,
        'country': _countryController.text,
        'city': _cityController.text,
        if (_birthday != null) 'birthday': _birthday!.toIso8601String(),
      };
      final updated = await _service.updateProfile(updates);
      setState(() => _profile = updated);
      _showSuccess('保存成功');
    } catch (e) {
      _showError('保存失败: $e');
    } finally {
      setState(() => _isSaving = false);
    }
  }

  Future<void> _updateStatus(OnlineStatus status) async {
    try {
      final updated = await _service.updateOnlineStatus(
        status,
        statusText: _statusTextController.text.isNotEmpty
            ? _statusTextController.text
            : null,
      );
      setState(() {
        _profile = updated;
        _onlineStatus = status;
      });
    } catch (e) {
      _showError('状态更新失败');
    }
  }

  Future<void> _pickAvatar() async {
    // 实际应使用 image_picker 插件
    // 此处为简化示例
    _showError('头像上传功能需要集成 image_picker');
  }

  Future<void> _addGroup() async {
    final name = await _showTextInputDialog('新建分组', '输入分组名称');
    if (name == null || name.isEmpty) return;
    try {
      final group = await _service.createFriendGroup(name);
      setState(() => _groups.add(group));
    } catch (e) {
      _showError('创建分组失败');
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('个人资料'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await AuthService().logout();
              if (mounted) Navigator.of(context).pushReplacementNamed('/login');
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 头像和基本信息
            Center(
              child: Column(
                children: [
                  GestureDetector(
                    onTap: _pickAvatar,
                    child: CircleAvatar(
                      radius: 50,
                      backgroundColor: Colors.grey[200],
                      backgroundImage: _profile?.avatarUrl.isNotEmpty == true
                          ? NetworkImage(_profile!.avatarUrl)
                          : null,
                      child: _profile?.avatarUrl.isEmpty != false
                          ? const Icon(Icons.person, size: 50)
                          : null,
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text('点击更换头像', style: TextStyle(color: Colors.grey)),
                  const SizedBox(height: 16),
                  Text(
                    _nicknameController.text,
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 4),
                  _buildStatusChip(),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // 基本信息表单
            _buildSection('基本信息', [
              _buildTextField('昵称', _nicknameController),
              _buildTextField('个性签名', _bioController, maxLines: 3),
              _buildGenderSelector(),
              _buildDatePicker(),
              _buildTextField('邮箱', _emailController, keyboardType: TextInputType.emailAddress),
            ]),

            // 在线状态
            _buildSection('在线状态', [
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: OnlineStatus.values
                    .where((s) => s != OnlineStatus.invisible) // 隐藏隐身选项
                    .map((s) => _buildStatusButton(s))
                    .toList(),
              ),
              const SizedBox(height: 12),
              _buildTextField('自定义状态', _statusTextController),
            ]),

            // 位置信息
            _buildSection('位置信息', [
              _buildTextField('国家/地区', _countryController),
              _buildTextField('城市', _cityController),
            ]),

            // 好友分组
            _buildSection('好友分组', [
              ..._groups.map((g) => ListTile(
                title: Text(g.groupName),
                dense: true,
              )),
              TextButton.icon(
                onPressed: _addGroup,
                icon: const Icon(Icons.add),
                label: const Text('新建分组'),
              ),
            ]),

            const SizedBox(height: 24),
            // 保存按钮
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSaving ? null : _saveProfile,
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: _isSaving
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('保存修改'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusChip() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: Color(_onlineStatus.color).withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              color: Color(_onlineStatus.color),
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            _onlineStatus.label,
            style: TextStyle(
              color: Color(_onlineStatus.color),
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatusButton(OnlineStatus status) {
    final isSelected = _onlineStatus == status;
    return FilterChip(
      label: Text(status.label),
      selected: isSelected,
      onSelected: (_) => _updateStatus(status),
      avatar: Container(
        width: 10,
        height: 10,
        decoration: BoxDecoration(
          color: Color(status.color),
          shape: BoxShape.circle,
        ),
      ),
      selectedColor: Color(status.color).withOpacity(0.2),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 12),
          child: Text(
            title,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(children: children),
          ),
        ),
      ],
    );
  }

  Widget _buildTextField(
    String label,
    TextEditingController controller, {
    int maxLines = 1,
    TextInputType? keyboardType,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        controller: controller,
        maxLines: maxLines,
        keyboardType: keyboardType,
        decoration: InputDecoration(
          labelText: label,
          border: const OutlineInputBorder(),
          isDense: true,
        ),
      ),
    );
  }

  Widget _buildGenderSelector() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          const Text('性别: '),
          const SizedBox(width: 12),
          ChoiceChip(
            label: const Text('未知'),
            selected: _gender == 0,
            onSelected: (_) => setState(() => _gender = 0),
          ),
          const SizedBox(width: 8),
          ChoiceChip(
            label: const Text('男'),
            selected: _gender == 1,
            onSelected: (_) => setState(() => _gender = 1),
          ),
          const SizedBox(width: 8),
          ChoiceChip(
            label: const Text('女'),
            selected: _gender == 2,
            onSelected: (_) => setState(() => _gender = 2),
          ),
        ],
      ),
    );
  }

  Widget _buildDatePicker() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: EdgeInsets.zero,
        title: const Text('生日'),
        subtitle: Text(
          _birthday != null
              ? '${_birthday!.year}-${_birthday!.month.toString().padLeft(2, '0')}-${_birthday!.day.toString().padLeft(2, '0')}'
              : '未设置',
        ),
        trailing: const Icon(Icons.calendar_today),
        onTap: () async {
          final date = await showDatePicker(
            context: context,
            initialDate: _birthday ?? DateTime(2000),
            firstDate: DateTime(1900),
            lastDate: DateTime.now(),
          );
          if (date != null) {
            setState(() => _birthday = date);
          }
        },
      ),
    );
  }

  Future<String?> _showTextInputDialog(String title, String hint) async {
    final controller = TextEditingController();
    return showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: TextField(
          controller: controller,
          decoration: InputDecoration(hintText: hint),
          autofocus: true,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, controller.text),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _showError(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), backgroundColor: Colors.red),
    );
  }

  void _showSuccess(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), backgroundColor: Colors.green),
    );
  }

  @override
  void dispose() {
    _nicknameController.dispose();
    _bioController.dispose();
    _emailController.dispose();
    _countryController.dispose();
    _cityController.dispose();
    _statusTextController.dispose();
    super.dispose();
  }
}
