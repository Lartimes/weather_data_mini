from datetime import datetime

import matplotlib.dates as mdates
import matplotlib.pyplot as plt
import numpy as np
import  WeatherPredict as predict
import WeatherClean as cln

columns = ["id", "日期", "最高温度", "最低温度",
           "天气", "风况", "城市"]

weather_conditions = ["雨", "阴", "云", "晴", "雪", "雾", "霾"]
date_index = [str(i) for i in range(2011, 2025)]
arr = cln.start()

for main_arr in arr:
    #     气温
    predict.start(main_arr)
    highest_list = np.array(list(main_arr[columns[2]]))
    lowest_list = np.array(list(main_arr[columns[3]]))
    date_list = list(main_arr[columns[1]])
    fit, ax = plt.subplots()
    ax.plot(date_list, highest_list)
    ax.plot(date_list, lowest_list)
    plt.show()

    for year in date_index:
        time = str(year) + "-01-01"
        date_object = datetime.strptime(time, "%Y-%m-%d")
        start = date_list.index(date_object)
        end = time = str(1 + int(year)) + "-01-01"
        date_object = datetime.strptime(time, "%Y-%m-%d")
        try:
            end = date_list.index(date_object)
        except:
            continue
        if start < 0:
            start = 0
        if end < 0:
            end = len(date_list) - 1
        year_list = main_arr.iloc[start: end]
        end += 1
        highest_slice = highest_list[start: end]
        lowest_slice = lowest_list[start: end]
        date_slice = date_list[start: end]
        fig, ax = plt.subplots()
        ax.plot(date_slice, highest_slice)
        ax.plot(date_slice, lowest_slice)
        plt.gca().xaxis.set_major_formatter(mdates.DateFormatter('%Y-%m-%d'))
        plt.gcf().autofmt_xdate()
        locator = mdates.DayLocator(interval=5)
        plt.gca().xaxis.set_major_locator(locator)
        plt.show()

    # weather =
    weather = np.array(list(main_arr[columns[4]]))
    # 天气
    count = [sum(1 for item in weather if weather_condition in item)
             for weather_condition in weather_conditions]

    plt.style.use('_mpl-gallery-nogrid')
    colors = plt.get_cmap('Blues')(np.linspace(0.2, 0.7, len(count)))
    fig, ax = plt.subplots()
    ax.pie(count, colors=colors, labels=weather_conditions,
           wedgeprops={"linewidth": 1, "edgecolor": "white"}, frame=True)
    plt.show()

#   TODO  风向级别类似，可视化未天气等未与时间匹配
