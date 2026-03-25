<template>
  <div class="reply-chain-page">
    <div class="page-header">
      <h2>消息回复链</h2>
      <button @click="refresh" class="refresh-btn">刷新</button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="chain-container">
      <div class="branch-view" v-if="branchView">
        <div class="branch-tree">
          <ChainNode :node="branchView.root" :expandedNodes="expandedNodes" 
            @toggle="toggleNode" @select="selectNode" />
        </div>
      </div>

      <div class="conversation-chains" v-if="!branchView">
        <div v-for="chain in currentChains" :key="chain.id" 
          class="chain-card" @click="viewChain(chain)">
          <div class="chain-info">
            <span class="depth-badge">深度 {{ chain.depth }}</span>
            <span class="node-count">{{ chain.branchNodes?.length || 0 }} 条回复</span>
          </div>
          <div class="chain-meta">
            <span>{{ chain.userNickname }}</span>
            <span>{{ formatTime(chain.createdAt) }}</span>
          </div>
        </div>
        <div v-if="currentChains.length === 0" class="empty-state">
          暂无回复链
        </div>
      </div>

      <button v-if="branchView" @click="closeBranchView" class="back-btn">
        返回列表
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { replyChainStore } from '../stores/reply-chain-store';

@Component
export default class ReplyChainPage extends Vue {
  replyChainStore = replyChainStore;

  get loading() { return replyChainStore.loading; }
  get error() { return replyChainStore.error; }
  get branchView() { return replyChainStore.branchView; }
  get expandedNodes() { return replyChainStore.expandedNodes; }

  get currentChains(): any[] {
    const conversationId = Number(this.$route.params.conversationId);
    return replyChainStore.conversationChains.get(conversationId) || [];
  }

  async mounted() {
    const conversationId = Number(this.$route.params.conversationId);
    if (conversationId) {
      await replyChainStore.loadConversationChains(conversationId);
    }
  }

  async refresh() {
    const conversationId = Number(this.$route.params.conversationId);
    if (conversationId) {
      await replyChainStore.loadConversationChains(conversationId);
    }
  }

  async viewChain(chain: any) {
    await replyChainStore.loadBranchTree(chain.rootMessageId);
  }

  closeBranchView() {
    replyChainStore.branchView = null;
  }

  toggleNode(nodeId: number) {
    replyChainStore.toggleNode(nodeId);
  }

  selectNode(chain: any) {
    replyChainStore.activeChain = chain;
  }

  formatTime(time: string): string {
    if (!time) return '';
    return new Date(time).toLocaleString('zh-CN');
  }
}
</script>

<style scoped>
.reply-chain-page { padding: 16px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; }
.refresh-btn { padding: 6px 16px; background: #1890ff; color: white; border: none; border-radius: 4px; cursor: pointer; }
.loading, .error, .empty-state { text-align: center; padding: 40px; color: #666; }
.error { color: #f5222d; }
.chain-card { background: #fafafa; border: 1px solid #e8e8e8; border-radius: 8px; padding: 12px; margin-bottom: 8px; cursor: pointer; transition: all 0.2s; }
.chain-card:hover { background: #e6f7ff; border-color: #1890ff; }
.chain-info { display: flex; gap: 8px; margin-bottom: 8px; }
.depth-badge { background: #1890ff; color: white; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.node-count { color: #666; font-size: 13px; }
.chain-meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; }
.back-btn { margin-top: 16px; padding: 8px 20px; background: #52c41a; color: white; border: none; border-radius: 4px; cursor: pointer; }
</style>
