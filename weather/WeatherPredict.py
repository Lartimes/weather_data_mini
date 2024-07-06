import pandas as pds
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
from sklearn.model_selection import train_test_split

import WeatherClean as cln

columns = ["id", "日期", "最高温度", "最低温度", "天气", "风况", "城市"]


def start(df: pds.DataFrame):
    df.drop(axis=1, labels=["天气", "风况", "城市"], inplace=True)
    df.dropna(inplace=True)
    df['日期'] = pds.to_datetime(df[columns[1]])  # 确保日期列是日期time类型
    df['month'] = df['日期'].dt.month
    df['dayofweek'] = df['日期'].dt.dayofweek
    print("=========")
    print(df)
    # 最高温度预测
    X_for_max = df.drop([columns[2], columns[1]], axis=1)
    y_max_temp = df[columns[2]]
    X_train1, X_test1, y_train_max_temp, y_test_max_temp = train_test_split(X_for_max, y_max_temp, test_size=0.2,
                                                                            random_state=42)
    X_for_min = df.drop([columns[3], columns[1]], axis=1)
    y_min_temp = df[columns[2]]
    X_train2, X_test2, y_train_min_temp, y_test_min_temp = train_test_split(X_for_min, y_min_temp, test_size=0.2,
                                                                            random_state=42)

    model_min_temp = RandomForestRegressor(n_estimators=100, random_state=42)
    model_max_temp = RandomForestRegressor(n_estimators=100, random_state=42)
    model_min_temp.fit(X_train2, y_train_min_temp)
    model_max_temp.fit(X_train1, y_train_max_temp)
    # 使用测试集进行预测
    y_pred_min_temp = model_min_temp.predict(X_test2)
    y_pred_max_temp = model_max_temp.predict(X_test1)
    print("max================")
    print(y_pred_max_temp)
    mse_max_temp = mean_squared_error(y_test_max_temp, y_pred_max_temp)
    print(f'Mean Squared Error for Maximum Temperature: {mse_max_temp}')
    # 访问预测结果中的第一个元素，并格式化为两位小数
    # 此处应该加与时间进行匹配index
    print(f'Predicted Maximum Temperature: {y_pred_max_temp[0]:.2f}°C')
    print("================end")

    print("min================")
    print(y_pred_min_temp)
    mse_min_temp = mean_squared_error(y_test_min_temp, y_pred_min_temp)
    print(f'Mean Squared Error for Minimum Temperature: {mse_min_temp}')
    # 访问预测结果中的第一个元素，并格式化为两位小数
    # 此处应该加与时间进行匹配index
    print(f'Predicted Maximum Temperature: {y_pred_min_temp[0]:.2f}°C')
    print("================end")


if __name__ == '__main__':
    arr = cln.start()
    for e in arr:
        start(e)
