from setuptools import find_packages
from setuptools import setup

setup(name='AssetCacheProxyApi', version='1.0', packages={'':'src'},
      install_requires=[
            "alpaca-trade-api",
            "falcon",
            "gunicorn"
      ])