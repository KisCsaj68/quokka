FROM python:3.10.7-slim-buster as build
RUN apt-get update
RUN apt-get install -y --no-install-recommends build-essential gcc

WORKDIR /usr/assetcache
RUN python -m venv /usr/assetcache/venv
ENV PATH="/usr/assetcache/venv/bin:$PATH"

COPY requirements.txt .
RUN pip install -r requirements.txt


FROM python:3.10.7-slim-buster
WORKDIR /usr/assetcache

COPY --from=build /usr/assetcache/venv ./venv
COPY . .

EXPOSE 5000
ENV PATH="/usr/assetcache/venv/bin:$PATH"

CMD ["python3", "assetcache.py"]