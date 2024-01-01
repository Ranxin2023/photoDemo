import pymysql
import secrets
class PhotoDatabase:
    def __init__(self, host=None, user=None, password=None) -> None:
        self.host = host or "localhost"
        self.user = user
        self.password = password

        self._conn = None
        self.cur_db_name = "chat_db"
        self.table_name = "photos"
        self._cursor = None
        self.connect_db()
        self.use_ssl = False
        self.ssl = {}

    def connect_db(self):
        self._conn = pymysql.connect(
            host=self.host,
            user=self.user,
            password=self.password,
            db=self.cur_db_name,
        )

        self._cursor = self._conn.cursor()

    def save_image_to_db(self, file_content, filename):
        # generate a hash code for image
        image_hash_code=secrets.token_hex(32)
        try:
            cmd = "INSERT INTO {} (photo_id, image_content, image_name) VALUES (%s, %s, %s)".format(self.table_name)
            self._cursor.execute(cmd, (image_hash_code, file_content,filename))
            self._conn.commit()
            return True, image_hash_code
        except Exception as e:
            print(f'An error occurred: {e}')
            return False, str(e)
        
    def fetch_image_data(self, image_hash_code):
        try:
            cmd = "SELECT image_content FROM {} WHERE photo_id=%s".format(self.table_name)
            self._cursor.execute(cmd, (image_hash_code))
            image_binary = self._cursor.fetchone()[0]

            if image_binary:
                # Prepare the response with the correct content type
                return True, image_binary, 'image/jpeg'
            else:
                return False, 'Image not found', 404
        except Exception as e:
            print(f'An error occurred: {e}')
            return False, str(e), 500

    