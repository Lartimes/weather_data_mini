import numpy as np
import pandas as pds

from SqlSession import SqlSession

# 包括数据去重、去噪、填充缺失值、处理异常值等。
columns = ["id", "日期", "最高温度", "最低温度", "天气", "风况", "城市"]


def to_df(city: str):
    result = SqlSession.getAllRows(city)
    df = pds.DataFrame(result, columns=columns)
    df.drop(columns[0], axis=1, inplace=True)
    # 进行日期、城市 排序
    df[columns[1]] = pds.to_datetime(df[columns[1]])
    df.sort_values(by=columns[1], inplace=True)
    df.sort_values(by=columns[-1], inplace=True)
    # 数据去重
    df.drop_duplicates(inplace=True)
    return df


# 处理缺失值
# 定义一个函数来计算前15天的平均值（如果可用）
def fill_with_15d_avg(row, df, index):
    # 获取当前行的日期
    current_date = row[columns[1]]
    # 计算前15天的日期
    start_date = current_date - pds.Timedelta(days=15)
    # 筛选前15天的数据
    recent_data = df[(df[columns[1]] >= start_date) & (df[columns[1]] < current_date)]
    # 计算平均值（如果至少有一个值）
    if not recent_data.empty:
        return int(recent_data[columns[index]].mean())
    else:
        return np.nan


def drop(df: pds.DataFrame):
    df.dropna(subset=[columns[1]], inplace=True)
    for i in range(2, len(columns)):
        key = columns[i]
        if i == 2:
            df.dropna(subset=[key], inplace=True)
            df = df[df[key] < 42]
            df[key] = df.apply(lambda row: fill_with_15d_avg(row, df, 2), axis=1)
        elif i == 3:
            df.dropna(subset=[key], inplace=True)
            df[key] = df.apply(lambda row: fill_with_15d_avg(row, df, 3), axis=1)
        elif i == 4:
            df[key] = df[key].fillna(method='ffill')
        elif i == 5:
            df[key] = df[key].fillna(method='ffill')
    df.sort_values(by=columns[1], inplace=True)
    return df


def start():
    arr = list()
    for city_tuple in SqlSession.getAllCities():
        for city in city_tuple:
            df = to_df(city)
            df = drop(df)
            arr.append(df)
    return arr


if __name__ == '__main__':
    start()
