<template>
  <v-app style="background: #f8f9fa;">
    <v-main>
      <div
        style="
          min-height: 100vh;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 24px;
          text-align: center;
        "
      >
        <!-- 404 大字 -->
        <div
          style="
            font-size: 120px;
            font-weight: 900;
            line-height: 1;
            background: linear-gradient(135deg, #1a73e8, #34a853);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 16px;
            letter-spacing: -4px;
          "
        >
          404
        </div>

        <!-- 插图 -->
        <div style="margin-bottom: 24px;">
          <svg width="120" height="80" viewBox="0 0 120 80" fill="none">
            <ellipse cx="60" cy="70" rx="50" ry="8" fill="#e8eaed"/>
            <rect x="30" y="20" width="60" height="45" rx="4" fill="#f1f3f4" stroke="#e8eaed" stroke-width="2"/>
            <rect x="35" y="26" width="50" height="30" rx="2" fill="white"/>
            <line x1="40" y1="32" x2="70" y2="32" stroke="#e8eaed" stroke-width="2" stroke-linecap="round"/>
            <line x1="40" y1="38" x2="65" y2="38" stroke="#e8eaed" stroke-width="2" stroke-linecap="round"/>
            <line x1="40" y1="44" x2="60" y2="44" stroke="#e8eaed" stroke-width="2" stroke-linecap="round"/>
            <circle cx="60" cy="35" r="12" fill="#fce8e6"/>
            <text x="56" y="40" font-size="14" fill="#ea4335" font-weight="bold">?</text>
          </svg>
        </div>

        <h1 style="font-size: 24px; font-weight: 700; color: #202124; margin: 0 0 8px;">
          页面不见了
        </h1>
        <p style="font-size: 15px; color: #80868b; margin: 0 0 32px; max-width: 360px; line-height: 1.6;">
          你访问的页面可能已被删除、移动，或者从未存在过。
        </p>

        <div style="display: flex; gap: 12px; flex-wrap: wrap; justify-content: center;">
          <v-btn
            color="primary"
            to="/"
            prepend-icon="mdi-home-outline"
            style="font-weight: 500;"
          >
            回到首页
          </v-btn>
          <v-btn
            variant="outlined"
            @click="$router.back()"
            prepend-icon="mdi-arrow-left"
            style="font-weight: 500; color: #5f6368;"
          >
            返回上页
          </v-btn>
        </div>

        <!-- 推荐链接 -->
        <div style="margin-top: 48px; display: flex; gap: 24px; flex-wrap: wrap; justify-content: center;">
          <router-link to="/archive" style="color: #1a73e8; font-size: 14px; text-decoration: none;">
            📚 文章归档
          </router-link>
          <router-link to="/search" style="color: #1a73e8; font-size: 14px; text-decoration: none;">
            🔍 搜索文章
          </router-link>
        </div>
      </div>
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: 'NotFoundView'
}
</script>
