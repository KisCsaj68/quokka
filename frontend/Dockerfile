FROM node:18.10-bullseye-slim

WORKDIR /usr/src/frontend

COPY package*.json ./

RUN npm ci --only=production
COPY --chown=node:node . .
USER node
EXPOSE 3000

CMD ["dumb-init","node","App.js"]