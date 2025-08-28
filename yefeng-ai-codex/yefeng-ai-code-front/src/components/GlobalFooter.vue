<template>
  <a-layout-footer class="footer">
    <!-- 顶部渐变条 -->
    <div class="footer-gradient"></div>

    <!-- 主要内容区域 -->
    <div class="footer-content">
      <div class="footer-columns">
        <!-- 第一列：平台介绍 -->
        <div class="footer-column">
          <h3 class="column-title">CodeLoom AI 应用生成平台</h3>
          <p class="platform-desc">一句话轻松创建网站应用,让AI为您的创意赋能</p>
          <div class="social-icons">
            <a class="social-icon wechat" href="#">
              <span class="icon-text">微信</span>
            </a>
            <a class="social-icon weibo" href="#">
              <span class="icon-text">微博</span>
            </a>
            <a class="social-icon gitee" href="#">
              <span class="icon-text">G</span>
            </a>
          </div>
        </div>

        <!-- 第二列：产品服务 -->
        <!-- <div class="footer-column">
          <h3 class="column-title">产品服务</h3>
          <ul class="footer-links">
            <li><a href="#">应用生成</a></li>
            <li><a href="#">产品文档</a></li>
            <li><a href="#">常见问题</a></li>
            <li><a href="#">技术支持</a></li>
          </ul>
        </div> -->

        <!-- 第三列：联系我们 -->
        <div class="footer-column">
          <h3 class="column-title">联系我们</h3>
          <ul class="footer-links">
            <li><a href="#">silend-wind@qq.com</a></li>
            <li><a href="#">Gitee</a></li>
            <li><a href="#">GitHub</a></li>
          </ul>
        </div>

        <!-- 第四列：关于 -->
        <div class="footer-column">
          <h3 class="column-title">关于</h3>
          <ul class="footer-links">
            <li><a href="#">关于我们</a></li>
            <li><a href="#">服务条款</a></li>
            <li><a href="#">隐私政策</a></li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <div class="footer-bottom-content">
        <div class="copyright-left">
          © 2025 CodeLoom. Sprinkle 版权所有.
        </div>
        <div class="copyright-right">
          Powered by Sprinkle
        </div>
      </div>
    </div>
  </a-layout-footer>
</template>

<script lang="ts" setup>
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: #f8f9fa;
  margin-top: auto;
  position: relative;
}

.footer-content {
  padding: 40px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-columns {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}

.footer-column {
  display: flex;
  flex-direction: column;
}

.column-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.platform-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.social-icons {
  display: flex;
  gap: 12px;
}

.social-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e0e0e0;
  color: #666;
  text-decoration: none;
  transition: all 0.3s ease;
}

.social-icon:hover {
  background: #1890ff;
  color: white;
}

.icon-text {
  font-size: 12px;
  font-weight: 500;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #1890ff;
}

.footer-bottom {
  border-top: 1px solid #e8e8e8;
  padding: 20px 24px;
}

.footer-bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.copyright-left,
.copyright-right {
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-columns {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .footer-bottom-content {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .footer-content {
    padding: 30px 16px;
  }

  .footer-bottom {
    padding: 16px;
  }
}
</style>
