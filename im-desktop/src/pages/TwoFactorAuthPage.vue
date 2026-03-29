<template>
  <div class="two-factor-page">
    <div class="card">
      <h2>两步验证 (2FA)</h2>

      <div v-if="store.step === 'idle'" class="step-idle">
        <p>保护您的账户安全，启用两步验证。</p>
        <div class="status-box" v-if="store.status">
          <div class="status-row">
            <span>状态:</span>
            <span :class="store.isEnabled ? 'green' : 'red'">
              {{ store.isEnabled ? '已启用' : '未启用' }}
            </span>
          </div>
          <div class="status-row" v-if="store.isEnabled">
            <span>备用码剩余:</span>
            <span>{{ store.backupCodesRemaining }} 个</span>
          </div>
        </div>
        <div class="actions">
          <button @click="startSetup" class="btn-primary">启用两步验证</button>
          <button
            v-if="store.isEnabled"
            @click="showDisable = true"
            class="btn-danger"
          >关闭两步验证</button>
        </div>
      </div>

      <div v-if="store.step === 'setup'" class="step-setup">
        <p>扫描下方二维码，或手动输入密钥：</p>
        <div v-if="store.setupData">
          <div class="qr-container" v-if="store.setupData.qrCodeBase64">
            <img :src="store.setupData.qrCodeBase64" alt="2FA QR Code" class="qr-image" />
          </div>
          <div class="manual-key">
            <label>手动密钥:</label>
            <code>{{ store.setupData.manualEntryKey }}</code>
          </div>
          <div class="backup-codes">
            <label>备用码 (请妥善保存):</label>
            <ul>
              <li v-for="code in store.setupData.backupCodes" :key="code">
                <code>{{ code }}</code>
              </li>
            </ul>
          </div>
        </div>
        <div class="verify-section">
          <label>输入6位验证码以确认启用:</label>
          <input
            v-model="verifyCode"
            type="text"
            maxlength="6"
            placeholder="000000"
            class="code-input"
            @keyup.enter="confirmEnable"
          />
          <button @click="confirmEnable" class="btn-primary" :disabled="store.isLoading">
            {{ store.isLoading ? '验证中...' : '确认启用' }}
          </button>
        </div>
        <div v-if="store.error" class="error-msg">{{ store.error }}</div>
      </div>

      <div v-if="store.step === 'verify'" class="step-verify">
        <p>请输入6位验证码完成验证</p>
        <input
          v-model="verifyCode"
          type="text"
          maxlength="6"
          placeholder="000000"
          class="code-input"
          @keyup.enter="confirmEnable"
        />
        <div class="verify-actions">
          <button @click="confirmEnable" class="btn-primary">验证</button>
          <button @click="store.reset()" class="btn-secondary">取消</button>
        </div>
      </div>

      <div v-if="store.step === 'complete'" class="step-complete">
        <div class="success-icon">✅</div>
        <p>两步验证已成功启用！</p>
        <button @click="store.reset()" class="btn-primary">完成</button>
      </div>

      <div v-if="showDisable" class="disable-modal">
        <h3>关闭两步验证</h3>
        <p>请输入验证码确认关闭:</p>
        <input
          v-model="disableCode"
          type="text"
          maxlength="10"
          placeholder="验证码或备用码"
          class="code-input"
        />
        <div class="modal-actions">
          <button @click="confirmDisable" class="btn-danger">确认关闭</button>
          <button @click="showDisable = false" class="btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useTwoFactorAuthStore } from '../stores/two-factor-auth-store';

const store = useTwoFactorAuthStore();
const verifyCode = ref('');
const disableCode = ref('');
const showDisable = ref(false);

const currentUserId = 1;

onMounted(() => {
  store.fetchStatus(currentUserId);
});

function startSetup() {
  store.startSetup(currentUserId);
}

async function confirmEnable() {
  if (verifyCode.value.length < 6) return;
  await store.verifyAndEnable(currentUserId, verifyCode.value);
}

async function confirmDisable() {
  if (!disableCode.value) return;
  await store.disable(currentUserId, disableCode.value);
  showDisable.value = false;
  disableCode.value = '';
}
</script>

<style scoped>
.two-factor-page { padding: 20px; }
.card { background: #fff; border-radius: 8px; padding: 24px; max-width: 480px; margin: 0 auto; }
h2 { margin-bottom: 16px; }
.status-box { background: #f5f5f5; padding: 12px; border-radius: 6px; margin: 16px 0; }
.status-row { display: flex; justify-content: space-between; margin-bottom: 6px; }
.green { color: #27ae60; font-weight: bold; }
.red { color: #e74c3c; font-weight: bold; }
.actions { display: flex; gap: 12px; margin-top: 16px; }
.btn-primary { background: #3498db; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
.btn-danger { background: #e74c3c; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
.btn-secondary { background: #95a5a6; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
.qr-container { text-align: center; margin: 16px 0; }
.qr-image { width: 180px; height: 180px; border: 1px solid #ddd; }
.manual-key { background: #f5f5f5; padding: 10px; border-radius: 6px; margin: 12px 0; }
.manual-key code { font-size: 13px; letter-spacing: 1px; }
.backup-codes ul { list-style: none; padding: 0; }
.backup-codes li { display: inline-block; margin: 4px; }
.backup-codes code { background: #f0f0f0; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
.verify-section { margin-top: 20px; }
.code-input { width: 100%; padding: 10px; font-size: 20px; text-align: center; letter-spacing: 4px; border: 1px solid #ddd; border-radius: 6px; margin: 8px 0; box-sizing: border-box; }
.verify-actions { display: flex; gap: 12px; }
.step-complete { text-align: center; }
.success-icon { font-size: 48px; margin-bottom: 16px; }
.error-msg { color: #e74c3c; margin-top: 8px; }
.disable-modal { margin-top: 20px; background: #fff0f0; padding: 16px; border-radius: 8px; }
.modal-actions { display: flex; gap: 12px; margin-top: 12px; }
</style>
