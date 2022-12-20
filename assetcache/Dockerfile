FROM python:3.10.8-slim-buster as build

COPY requirements.txt .
RUN pip install -r requirements.txt

FROM build AS final
RUN apt-get update && apt-get install -y procps
WORKDIR /usr/assetcache
COPY --chmod=755 start.sh .
COPY gunicorn.conf.py .
COPY wsgi.py .
COPY .env .
COPY env.py .
COPY PrimitiveJsonDB.json .
COPY src/ src/
EXPOSE 8000
ENTRYPOINT ["./start.sh"]
