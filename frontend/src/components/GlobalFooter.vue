<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        © {{ year }} AI Router · 统一 AI 模型接入网关
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  text-align: center;
  padding: 20px;
  margin-top: 40px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}

.copyright {
  margin: 0;
  color: #666;
  font-size: 14px;
}
</style>
