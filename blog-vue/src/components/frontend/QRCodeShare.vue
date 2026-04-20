<template>
  <!-- 二维码分享弹窗 -->
  <div style="display: inline-block;">
    <button
      @click="show = true"
      style="background: none; border: none; cursor: pointer; padding: 4px; border-radius: 4px; color: #5f6368; font-size: 12px; display: flex; align-items: center; gap: 3px; transition: background 0.15s;"
      class="share-btn"
      title="二维码分享"
    >
      <v-icon size="14">mdi-qrcode</v-icon>
      二维码
    </button>

    <!-- 弹窗 -->
    <v-dialog v-model="show" max-width="280">
      <v-card rounded="xl" style="text-align: center; padding: 24px;">
        <div style="font-size: 15px; font-weight: 600; color: #202124; margin-bottom: 16px;">扫码分享文章</div>
        <!-- 使用 qrserver.com 免费 API 生成二维码 -->
        <img
          :src="qrUrl"
          style="width: 180px; height: 180px; border-radius: 8px; border: 1px solid #e8eaed;"
          alt="二维码"
        />
        <div style="font-size: 12px; color: #80868b; margin-top: 12px; word-break: break-all; max-width: 220px; margin-left: auto; margin-right: auto;">
          {{ currentUrl }}
        </div>
        <v-btn variant="text" color="primary" @click="show = false" style="margin-top: 12px;">关闭</v-btn>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
export default {
  name: 'QRCodeShare',
  data: function() {
    return {
      show: false,
      currentUrl: ''
    }
  },
  computed: {
    qrUrl: function() {
      var url = encodeURIComponent(this.currentUrl || window.location.href)
      return 'https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=' + url
    }
  },
  mounted: function() {
    this.currentUrl = window.location.href
  },
  watch: {
    show: function(val) {
      if (val) this.currentUrl = window.location.href
    }
  }
}
</script>
