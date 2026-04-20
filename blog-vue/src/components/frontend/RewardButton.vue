<template>
  <!-- 打赏按钮 -->
  <div style="display: inline-block;">
    <button
      @click="show = true"
      style="
        display: flex; align-items: center; gap: 8px;
        padding: 10px 20px;
        border-radius: 24px;
        border: 2px solid #f59e0b;
        cursor: pointer;
        font-size: 15px;
        font-weight: 600;
        transition: all 0.2s;
        background: white;
        color: #f59e0b;
      "
      @mouseenter="$event.currentTarget.style.background='#fffbeb'"
      @mouseleave="$event.currentTarget.style.background='white'"
    >
      ☕ 打赏
    </button>

    <v-dialog v-model="show" max-width="360">
      <v-card rounded="xl" style="text-align: center; padding: 24px;">
        <div style="font-size: 18px; font-weight: 700; color: #202124; margin-bottom: 4px;">☕ 请作者喝杯咖啡</div>
        <div style="font-size: 13px; color: #80868b; margin-bottom: 20px;">你的支持是我持续创作的动力！</div>

        <div class="d-flex justify-center" style="gap: 24px;">
          <!-- 微信 -->
          <div style="text-align: center;">
            <div style="width: 120px; height: 120px; background: #f1f3f4; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-bottom: 8px; border: 1px solid #e8eaed;">
              <div style="text-align: center;">
                <v-icon size="32" color="success">mdi-wechat</v-icon>
                <div style="font-size: 10px; color: #80868b; margin-top: 4px;">微信收款码</div>
                <div style="font-size: 9px; color: #bdc1c6;">（请在系统设置中配置）</div>
              </div>
            </div>
            <div style="font-size: 12px; color: #34a853; font-weight: 500;">微信支付</div>
          </div>

          <!-- 支付宝 -->
          <div style="text-align: center;">
            <div style="width: 120px; height: 120px; background: #f1f3f4; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-bottom: 8px; border: 1px solid #e8eaed;">
              <div style="text-align: center;">
                <v-icon size="32" color="primary">mdi-alpha-a-circle</v-icon>
                <div style="font-size: 10px; color: #80868b; margin-top: 4px;">支付宝收款码</div>
                <div style="font-size: 9px; color: #bdc1c6;">（请在系统设置中配置）</div>
              </div>
            </div>
            <div style="font-size: 12px; color: #1a73e8; font-weight: 500;">支付宝</div>
          </div>
        </div>

        <v-btn variant="text" @click="show = false" style="margin-top: 16px;">关闭</v-btn>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'RewardButton',
  data: function() {
    return { show: false }
  }
}
</script>
