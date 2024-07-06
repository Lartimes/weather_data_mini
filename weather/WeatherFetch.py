import os
import random
from datetime import datetime
from multiprocessing.pool import ThreadPool

import httpx
import requests
from bs4 import BeautifulSoup

chunk_dir = "./weather/"
base_uri = 'https://lishi.tianqi.com/'
header = {
    "Host": "lishi.tianqi.com",
    "Connection": "keep-alive",
    "Upgrade-Insecure-Requests": "1",
    'Content-Encoding': 'gzip',
    'Content-Type': 'text/html; charset=utf-8',
    'Vary': 'Accept-Encoding',
    'Set-Cookie': 'cityPy=ganzhou; expires=Mon, 31-Jul-2025 03:22:01 GMT; Max-Age=2419200; path=/; domain=.tianqi.com',
    'origin': 'https://lishi.tianqi.com',
    'referer': base_uri,
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0'
}

proxies = ['HTTP://110.243.30.23:9999', 'HTTP://222.189.191.206:9999', 'HTTP://118.212.104.138:9999',
           'HTTP://182.149.83.97:9999', 'HTTP://106.42.163.100:9999', 'HTTP://120.83.107.69:9999',
           'HTTP://60.13.42.135:9999', 'HTTP://60.205.188.24:3128', 'HTTP://113.195.232.23:9999',
           'HTTP://59.62.36.74:9000', 'HTTP://218.2.226.42:80']

proxy = {'HTTP': random.choice(proxies)}
error_urls = list()
date_patterns = list()


def get_request_error_url(url: str):
    global proxy
    proxy = {'HTTP': random.choice(proxies)}
    get_request(url)


def get_request(url: str):
    r = requests.get(url, headers=header, proxies=proxy, timeout=5)
    try:
        r.raise_for_status()
    except:
        error_urls.append(url)
        raise httpx.HTTPError
    if r.status_code == 200:
        bs = BeautifulSoup(r.text, "html5lib")
        try:
            info = bs.find('div', attrs={'class': 'tian_three'})
            months = info.find('ul', attrs={'class': 'thrui'})
            days = months.find_all('li')
            tmp = url[len(base_uri)::].replace("/", "").replace(".html", '.txt')
            with open(chunk_dir + tmp, 'w') as fl:
                for day in days:
                    day_info = ""
                    contents = day.text.strip().split("\n")
                    for i in range(len(contents)):
                        if i == 0:
                            day_info += contents[i].strip()[:10]
                            day_info += ','
                        elif i == 1 or i == 2:
                            tmp = contents[i].strip()
                            day_info += tmp[:-1:]
                            day_info += ','
                        elif i == 3:
                            day_info += contents[i].strip()
                            day_info += ','
                        else:
                            day_info += contents[i].strip()
                            day_info += '\n'
                    fl.write(day_info)
                    fl.flush()
            #    TODO 不加锁.合并小文件
            if len(str(info.text)) == 0 or info is None:
                error_urls.append(url)
        except:
            error_urls.append(url)
    else:
        print(r.status_code, " : ", url)


def task(n: int):
    url = base_uri + date_patterns[n] + ".html"
    get_request(url)


def fetch_weather(date: datetime, before_gap: int, city: str):
    year = date.year
    if datetime.now().year == year:
        month = date.month
        for i in range(1, month + 1):
            tmp = str(year)
            if i < 10:
                tmp += "0"
            tmp += str(i)
            tmp = city + "/" + tmp
            date_patterns.append(tmp)
    for i in range(before_gap):
        year -= 1
        for i in range(1, 13):
            tmp = str(year)
            if i < 10:
                tmp += "0"
            tmp += str(i)
            tmp = city + "/" + tmp
            date_patterns.append(tmp)
    # 创建一个线程池
    pool = ThreadPool(20)
    pool.map(task, range(len(date_patterns)))
    pool.close()


def slove_error():
    max_retry = 5
    while max_retry:
        if len(error_urls) == 0:
            return 1
        with open("./error_urls.txt", 'w') as fl:
            for error_url in error_urls:
                fl.write(error_url + "\r\n")
                fl.flush()
        error_urls.clear()
        with open("./error_urls.txt", 'r') as fl:
            for line in fl.readlines():
                if len(line.strip()):
                    get_request_error_url(line)
        max_retry -= 1
    return 0


def merge_data(base_city: str, city: str):
    files = os.listdir(chunk_dir)
    with open(base_city + ".txt", 'w', encoding='utf8') as wfl:
        for file in files:
            if file.startswith(base_city):
                try:
                    with open(chunk_dir + file, 'r') as fl:
                        for readline in fl.readlines():
                            new_line = readline.replace('\n', ',' + city + '\n')
                            wfl.write(new_line)
                            wfl.flush()
                    os.remove(file)
                except:
                    pass


if __name__ == '__main__':
    cities_map = { 'shijiazhuang': '石家庄'}
    # cities_map = {'yudu': '于都', 'shijiazhuang': '石家庄'}
    date = datetime.now()
    before_gap = 13  # 距今查询年数
    if os.path.exists(chunk_dir):
        pass
    else:
        os.makedirs(chunk_dir)
    for base_city in cities_map.items():
        fetch_weather(date, before_gap, base_city[0])
        if slove_error() == 1:
            print("success")
        merge_data(base_city[0], base_city[1])
    print("总条数应该为:", 163 * len(cities_map))
