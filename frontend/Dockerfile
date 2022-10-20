ARG NODE_VERSION=18.10
FROM node:${NODE_VERSION}-alpine

WORKDIR /frontend

COPY package.json ./
COPY package-lock.json ./
COPY public/ public/
COPY src/ src/
#COPY node_modules/ ./


#RUN npm i
RUN --mount=type=cache,target=/frontend/.npm npm set cache /frontend/.npm && npm install
EXPOSE 3000
ENTRYPOINT ["npm"]
CMD ["run", "start"]


