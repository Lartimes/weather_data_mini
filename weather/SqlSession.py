import pymysql


class SqlSession:
    # username password database host
    # properties = resource_bundle.get_bundle()
    # username = properties['mysql']['username']
    username = 'root'
    password = '307314'
    database = 'weather'
    host = 'localhost'

    @classmethod
    def get_conn(cls, host=host, user=username,
                 password=password,
                 database=database,
                 port=3306):
        return pymysql.connect(host=host,
                               user=user,
                               password=password,
                               database=database,
                               port=port)

    @staticmethod
    def getAllRows(city: str):
        conn = None
        try:
            conn = SqlSession.get_conn()
            cursor = conn.cursor()
            sql = "select * from daily_weather where position = %s"
            cursor.execute(sql, city)
            result = cursor.fetchall()
            return result
        except:
            if conn is not None:
                conn.rollback()
        finally:
            if conn is not None:
                conn.close()

    @staticmethod
    def getAllCities():
        conn = None
        try:
            conn = SqlSession.get_conn()
            cursor = conn.cursor()
            sql = "select distinct  position from daily_weather"
            cursor.execute(sql)
            result = cursor.fetchall()
            return result
        except:
            if conn is not None:
                conn.rollback()
        finally:
            if conn is not None:
                conn.close()

if __name__ == '__main__':
    for getAllCity in SqlSession.getAllCities():
        print(getAllCity[0])