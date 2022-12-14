FROM python:3.10.8-slim-buster as build

COPY requirements.txt .
RUN pip install -r requirements.txt

FROM build AS final
RUN apt-get update && apt-get install -y procps
WORKDIR /usr/assetcache
COPY gunicorn.conf.py .
COPY wsgi.py .
COPY env.py .
COPY PrimitiveJsonDB.json .
COPY src/ src/
COPY .env .
EXPOSE 8000
ENTRYPOINT ["gunicorn"]
CMD ["wsgi:application"]
#CMD ["-b", "0.0.0.0:8000", "--workers=1", "--threads=1","wsgi:application"]
