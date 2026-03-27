import 'package:flutter/material.dart';
import '../models/ai_assistant.dart';

/// AI助手选择器组件
class AIAssistantSelector extends StatefulWidget {
  final List<AIAssistantModel> assistants;
  final AIAssistantModel? selectedAssistant;
  final ValueChanged<AIAssistantModel> onAssistantSelected;
  final VoidCallback? onCreateCustom;

  const AIAssistantSelector({
    super.key,
    required this.assistants,
    this.selectedAssistant,
    required this.onAssistantSelected,
    this.onCreateCustom,
  });

  @override
  State<AIAssistantSelector> createState() => _AIAssistantSelectorState();
}

class _AIAssistantSelectorState extends State<AIAssistantSelector> {
  String _searchQuery = '';
  AIAssistantCapability? _selectedCapability;

  List<AIAssistantModel> get filteredAssistants {
    return widget.assistants.where((assistant) {
      final matchesSearch = _searchQuery.isEmpty ||
          assistant.name.toLowerCase().contains(_searchQuery.toLowerCase()) ||
          assistant.description.toLowerCase().contains(_searchQuery.toLowerCase());
      
      final matchesCapability = _selectedCapability == null ||
          assistant.capabilities.contains(_selectedCapability);
      
      return matchesSearch && matchesCapability;
    }).toList();
  }

  Map<String, List<AIAssistantModel>> get groupedAssistants {
    final groups = <String, List<AIAssistantModel>>{};
    
    for (final assistant in filteredAssistants) {
      final providerName = _getProviderDisplayName(assistant.provider);
      groups.putIfAbsent(providerName, () => []).add(assistant);
    }
    
    return groups;
  }

  String _getProviderDisplayName(AIProvider provider) {
    switch (provider) {
      case AIProvider.openai:
        return 'OpenAI';
      case AIProvider.claude:
        return 'Claude';
      case AIProvider.gemini:
        return 'Gemini';
      case AIProvider.local:
        return '本地模型';
      case AIProvider.custom:
        return '自定义';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // 搜索栏
        Padding(
          padding: const EdgeInsets.all(16),
          child: TextField(
            onChanged: (value) => setState(() => _searchQuery = value),
            decoration: InputDecoration(
              hintText: '搜索AI助手...',
              prefixIcon: const Icon(Icons.search),
              suffixIcon: _searchQuery.isNotEmpty
                  ? IconButton(
                      icon: const Icon(Icons.clear),
                      onPressed: () => setState(() => _searchQuery = ''),
                    )
                  : null,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
          ),
        ),

        // 能力筛选
        SizedBox(
          height: 50,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            children: [
              _buildCapabilityChip(null, '全部'),
              _buildCapabilityChip(AIAssistantCapability.textGeneration, '文本'),
              _buildCapabilityChip(AIAssistantCapability.imageAnalysis, '图像'),
              _buildCapabilityChip(AIAssistantCapability.audioTranscription, '语音'),
              _buildCapabilityChip(AIAssistantCapability.codeGeneration, '代码'),
              _buildCapabilityChip(AIAssistantCapability.reasoning, '推理'),
            ],
          ),
        ),

        const Divider(),

        // 助手列表
        Expanded(
          child: ListView.builder(
            itemCount: groupedAssistants.length + (widget.onCreateCustom != null ? 1 : 0),
            itemBuilder: (context, index) {
              if (widget.onCreateCustom != null && index == groupedAssistants.length) {
                return _buildCreateCustomButton();
              }

              final entry = groupedAssistants.entries.elementAt(index);
              return _buildGroupSection(entry.key, entry.value);
            },
          ),
        ),
      ],
    );
  }

  Widget _buildCapabilityChip(AIAssistantCapability? capability, String label) {
    final isSelected = _selectedCapability == capability;
    
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: FilterChip(
        selected: isSelected,
        label: Text(label),
        onSelected: (selected) {
          setState(() => _selectedCapability = selected ? capability : null);
        },
        selectedColor: Theme.of(context).colorScheme.primaryContainer,
        checkmarkColor: Theme.of(context).colorScheme.primary,
      ),
    );
  }

  Widget _buildGroupSection(String providerName, List<AIAssistantModel> assistants) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Text(
            providerName,
            style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  color: Theme.of(context).colorScheme.primary,
                  fontWeight: FontWeight.bold,
                ),
          ),
        ),
        ...assistants.map((assistant) => _buildAssistantTile(assistant)),
      ],
    );
  }

  Widget _buildAssistantTile(AIAssistantModel assistant) {
    final isSelected = widget.selectedAssistant?.id == assistant.id;
    
    return ListTile(
      leading: CircleAvatar(
        backgroundImage: assistant.avatarUrl != null
            ? NetworkImage(assistant.avatarUrl!)
            : null,
        child: assistant.avatarUrl == null
            ? Text(assistant.name.substring(0, 1).toUpperCase())
            : null,
      ),
      title: Text(assistant.name),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            assistant.description,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: 4),
          Wrap(
            spacing: 4,
            children: assistant.capabilities
                .take(3)
                .map((cap) => _buildCapabilityBadge(cap))
                .toList(),
          ),
        ],
      ),
      trailing: isSelected
          ? Icon(Icons.check_circle, color: Theme.of(context).colorScheme.primary)
          : null,
      selected: isSelected,
      onTap: () => widget.onAssistantSelected(assistant),
    );
  }

  Widget _buildCapabilityBadge(AIAssistantCapability capability) {
    final (icon, label, color) = _getCapabilityInfo(capability);
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 12, color: color),
          const SizedBox(width: 2),
          Text(
            label,
            style: TextStyle(fontSize: 10, color: color),
          ),
        ],
      ),
    );
  }

  (IconData, String, Color) _getCapabilityInfo(AIAssistantCapability capability) {
    final colorScheme = Theme.of(context).colorScheme;
    
    switch (capability) {
      case AIAssistantCapability.textGeneration:
        return (Icons.text_fields, '文本', colorScheme.primary);
      case AIAssistantCapability.imageGeneration:
      case AIAssistantCapability.imageAnalysis:
        return (Icons.image, '图像', Colors.blue);
      case AIAssistantCapability.audioTranscription:
      case AIAssistantCapability.audioSynthesis:
        return (Icons.mic, '语音', Colors.green);
      case AIAssistantCapability.videoAnalysis:
        return (Icons.videocam, '视频', Colors.orange);
      case AIAssistantCapability.codeGeneration:
        return (Icons.code, '代码', Colors.purple);
      case AIAssistantCapability.translation:
        return (Icons.translate, '翻译', Colors.teal);
      case AIAssistantCapability.summarization:
        return (Icons.summarize, '摘要', Colors.indigo);
      case AIAssistantCapability.reasoning:
        return (Icons.psychology, '推理', Colors.red);
    }
  }

  Widget _buildCreateCustomButton() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: OutlinedButton.icon(
        onPressed: widget.onCreateCustom,
        icon: const Icon(Icons.add),
        label: const Text('创建自定义助手'),
        style: OutlinedButton.styleFrom(
          minimumSize: const Size(double.infinity, 48),
        ),
      ),
    );
  }
}

/// 底部弹出的AI助手选择器
Future<AIAssistantModel?> showAIAssistantSelector(
  BuildContext context, {
  required List<AIAssistantModel> assistants,
  AIAssistantModel? selectedAssistant,
}) async {
  return showModalBottomSheet<AIAssistantModel>(
    context: context,
    isScrollControlled: true,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
    ),
    builder: (context) => DraggableScrollableSheet(
      initialChildSize: 0.6,
      minChildSize: 0.4,
      maxChildSize: 0.9,
      expand: false,
      builder: (context, scrollController) {
        return Column(
          children: [
            Container(
              width: 40,
              height: 4,
              margin: const EdgeInsets.symmetric(vertical: 8),
              decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(2),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Text(
                '选择AI助手',
                style: Theme.of(context).textTheme.titleLarge,
              ),
            ),
            Expanded(
              child: AIAssistantSelector(
                assistants: assistants,
                selectedAssistant: selectedAssistant,
                onAssistantSelected: (assistant) {
                  Navigator.pop(context, assistant);
                },
              ),
            ),
          ],
        );
      },
    ),
  );
}
