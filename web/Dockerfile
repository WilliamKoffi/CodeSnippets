FROM oven/bun:1-alpine
WORKDIR /app

# Install dependencies first for better caching
COPY package.json bun.lock ./
RUN bun install

# Copy source code and build/serve the app
COPY . .

EXPOSE 4200

# Start Angular dev server and allow external host access (required for Docker)
CMD ["bun", "run", "start", "--", "--host", "0.0.0.0", "--disable-host-check"]
