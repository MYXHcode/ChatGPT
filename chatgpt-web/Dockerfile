# 使用 Node.js 18 Alpine 作为基础镜像
FROM node:18-alpine AS base

# 仅在需要时安装依赖项
FROM base AS deps

# 检查 https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine 以了解为什么可能需要 libc6-compat。
RUN apk add --no-cache libc6-compat

# 设置工作目录
WORKDIR /app

# 复制 package.json、yarn.lock*、package-lock.json*和pnpm-lock.yaml* 到容器中
# Install dependencies based on the preferred package manager
COPY package.json yarn.lock* package-lock.json* pnpm-lock.yaml* ./

# RUN \
#  if [ -f yarn.lock ]; then yarn --frozen-lockfile; \
#  elif [ -f package-lock.json ]; then npm ci; \
#  elif [ -f pnpm-lock.yaml ]; then yarn global add pnpm && pnpm i --frozen-lockfile; \
#  else echo "Lockfile not found." && exit 1; \
#  fi

# 安装依赖
RUN yarn config set registry 'https://registry.npmmirror.com/'
RUN yarn install

# 将应用程序代码复制到容器中
# 仅在需要时重新生成源代码
FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# 构建 TypeScript 代码
# Next.js 收集关于一般用途的完全匿名的遥测数据。
# 在这里了解更多信息：https:nextjs.orgtelemetry
# 如果您想在构建过程中禁用遥测，请取消注释以下行。
# ENV NEXT_TELEMETRY_DISABLED 1
RUN yarn build

# 如果在上面使用 npm 注释，请在下面使用
# RUN npm run build

# 使用多阶段构建
# 生产映像，复制所有文件并运行下一个
FROM base AS runner
WORKDIR /app

# 从构建阶段复制构建的产物
# ENV NODE_ENV 生产
# 如果要在运行时禁用遥测，请取消注释以下行。
# ENV NEXT_TELEMETRY_DISABLED 1

# RUN addgroup --system --gid 1001 nodejs
# RUN adduser --system --uid 1001 nextjs

# COPY --from=builder /app/public ./public

# 设置预呈现缓存的正确权限
# RUN mkdir .next
# RUN chown nextjs:nodejs .next

# 自动利用输出跟踪来减小图像大小
# https://nextjs.org/docs/advanced-features/output-file-tracing
# COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
# COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/.next/server ./.next/server

# 用户 nextjs
EXPOSE 3000

ENV PORT 3000

# 将主机名设置为 localhost
ENV HOSTNAME "0.0.0.0"

CMD ["node", "server.js"]
