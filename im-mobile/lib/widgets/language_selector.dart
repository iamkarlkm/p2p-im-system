import 'package:flutter/material.dart';
import '../services/translation_service.dart';

class LanguageSelector extends StatelessWidget {
  final String selectedLanguage;
  final ValueChanged<String> onLanguageChanged;
  final Map<String, String> languages;
  final String title;
  final bool showAuto;
  
  const LanguageSelector({
    Key? key,
    required this.selectedLanguage,
    required this.onLanguageChanged,
    required this.languages,
    this.title = '选择语言',
    this.showAuto = true,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(languages[selectedLanguage] ?? selectedLanguage),
      trailing: const Icon(Icons.arrow_drop_down),
      onTap: () => _showLanguagePicker(context),
    );
  }
  
  void _showLanguagePicker(BuildContext context) {
    final filteredLanguages = showAuto
        ? languages
        : Map.from(languages)..remove('auto');
    
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.6,
        minChildSize: 0.4,
        maxChildSize: 0.8,
        expand: false,
        builder: (context, scrollController) => Column(
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                border: Border(
                  bottom: BorderSide(color: Colors.grey[300]!),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: () => Navigator.pop(context),
                  ),
                ],
              ),
            ),
            Expanded(
              child: ListView.builder(
                controller: scrollController,
                itemCount: filteredLanguages.length,
                itemBuilder: (context, index) {
                  final entry = filteredLanguages.entries.elementAt(index);
                  final isSelected = entry.key == selectedLanguage;
                  return ListTile(
                    title: Text(entry.value),
                    trailing: isSelected
                        ? Icon(Icons.check, color: Theme.of(context).primaryColor)
                        : null,
                    tileColor: isSelected ? Colors.blue.withOpacity(0.1) : null,
                    onTap: () {
                      onLanguageChanged(entry.key);
                      Navigator.pop(context);
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class LanguageSelectorBar extends StatelessWidget {
  final String sourceLanguage;
  final String targetLanguage;
  final ValueChanged<String> onSourceChanged;
  final ValueChanged<String> onTargetChanged;
  final VoidCallback onSwap;
  
  const LanguageSelectorBar({
    Key? key,
    required this.sourceLanguage,
    required this.targetLanguage,
    required this.onSourceChanged,
    required this.onTargetChanged,
    required this.onSwap,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    final service = TranslationService();
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: _buildLanguageButton(
              context,
              service.supportedLanguages[sourceLanguage] ?? '自动检测',
              () => _showLanguagePicker(context, true),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            child: IconButton(
              icon: const Icon(Icons.swap_horiz),
              onPressed: sourceLanguage == 'auto' ? null : onSwap,
              color: sourceLanguage == 'auto' ? Colors.grey : null,
            ),
          ),
          Expanded(
            child: _buildLanguageButton(
              context,
              service.supportedLanguages[targetLanguage] ?? '简体中文',
              () => _showLanguagePicker(context, false),
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildLanguageButton(BuildContext context, String text, VoidCallback onTap) {
    return Material(
      color: Colors.grey[100],
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Flexible(
                child: Text(
                  text,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              const SizedBox(width: 4),
              const Icon(Icons.arrow_drop_down, size: 18),
            ],
          ),
        ),
      ),
    );
  }
  
  void _showLanguagePicker(BuildContext context, bool isSource) {
    final service = TranslationService();
    final languages = isSource
        ? service.supportedLanguages
        : (Map.from(service.supportedLanguages)..remove('auto'));
    
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => LanguagePickerSheet(
        languages: languages,
        selectedLanguage: isSource ? sourceLanguage : targetLanguage,
        title: isSource ? '选择源语言' : '选择目标语言',
        onLanguageSelected: isSource ? onSourceChanged : onTargetChanged,
      ),
    );
  }
}

class LanguagePickerSheet extends StatelessWidget {
  final Map<String, String> languages;
  final String selectedLanguage;
  final String title;
  final ValueChanged<String> onLanguageSelected;
  
  const LanguagePickerSheet({
    Key? key,
    required this.languages,
    required this.selectedLanguage,
    required this.title,
    required this.onLanguageSelected,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    final popularLanguages = ['zh-CN', 'en', 'ja', 'ko', 'auto'];
    final popular = languages.entries
        .where((e) => popularLanguages.contains(e.key))
        .toList();
    final others = languages.entries
        .where((e) => !popularLanguages.contains(e.key))
        .toList();
    
    return DraggableScrollableSheet(
      initialChildSize: 0.7,
      minChildSize: 0.5,
      maxChildSize: 0.9,
      expand: false,
      builder: (context, scrollController) => Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              border: Border(
                bottom: BorderSide(color: Colors.grey[300]!),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(title, style: Theme.of(context).textTheme.titleLarge),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView(
              controller: scrollController,
              children: [
                if (popular.isNotEmpty) ...[
                  const Padding(
                    padding: EdgeInsets.fromLTRB(16, 16, 16, 8),
                    child: Text(
                      '常用语言',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.grey,
                      ),
                    ),
                  ),
                  ...popular.map((e) => _buildLanguageTile(context, e)),
                ],
                if (others.isNotEmpty) ...[
                  const Padding(
                    padding: EdgeInsets.fromLTRB(16, 16, 16, 8),
                    child: Text(
                      '所有语言',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.grey,
                      ),
                    ),
                  ),
                  ...others.map((e) => _buildLanguageTile(context, e)),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildLanguageTile(BuildContext context, MapEntry<String, String> entry) {
    final isSelected = entry.key == selectedLanguage;
    return ListTile(
      title: Text(entry.value),
      trailing: isSelected
          ? Icon(Icons.check, color: Theme.of(context).primaryColor)
          : null,
      tileColor: isSelected ? Colors.blue.withOpacity(0.1) : null,
      onTap: () {
        onLanguageSelected(entry.key);
        Navigator.pop(context);
      },
    );
  }
}
