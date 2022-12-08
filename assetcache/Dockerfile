FROM python:3.10.7-slim-buster as build

COPY requirements.txt .
RUN pip install -r requirements.txt

FROM build AS final
WORKDIR /usr/assetcache
COPY wsgi.py .
COPY src/ src/
COPY .env .
COPY PrimitiveJsonDB.json .
EXPOSE 8000
ENTRYPOINT ["gunicorn"]
CMD ["-b", "0.0.0.0:8000", "wsgi:application"]


#RUN apt-get update
#RUN apt-get install -y --no-install-recommends build-essential gcc
#
#WORKDIR /usr/assetcache
#RUN python -m venv /usr/assetcache/venv
#ENV PATH="/usr/assetcache/venv/bin:$PATH"
#
#COPY requirements.txt .
#RUN pip install -r requirements.txt
#
#
#FROM python:3.10.7-slim-buster
#WORKDIR /usr/assetcache
#
#COPY --from=build /usr/assetcache/venv ./venv
#COPY . .
#
#EXPOSE 5000
#ENV PATH="/usr/assetcache/venv/bin:$PATH"
#
#CMD ["python3", "assetcache.py"]