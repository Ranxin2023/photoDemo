from werkzeug.wrappers import Request, Response
# from werkzeug.serving import run_simple
from werkzeug.utils import secure_filename
from database import PhotoDatabase
import os, json
class App:
    def __init__(self) -> None:
        self.localhost = "127.0.0.1"
        self.db_username = "root"
        self.db_password = "R@ndyli94041424"
        self.db = PhotoDatabase(self.localhost, self.db_username, self.db_password)

    def init_request(self, request=None):
        # print(f"connection successfully request is:{request}")
        resp = None
        if request.method == "POST":
            resp = self.handle_post(request=request)
        else:
            resp = Response("Method Not Allowed", status=405)
        return resp


    def handle_post(self, request):
        
        if 'file' in request.files:
            print("enter bytestream")
            file = request.files['file']
            filename = secure_filename(file.filename)
            file_content = file.stream.read()  # Read file content as binary
            success_store, photoid_errmsg=self.db.save_image_to_db(file_content, filename)
            if success_store:
                
                response_data = json.dumps({
                    "success": True,
                    "photo id":photoid_errmsg, 
                    "error msg": 'File successfully uploaded and stored in database'
                })
                return Response(response_data, content_type="application/json", status=200)
            else:
                response_data = json.dumps(
                {
                    "success": True,
                    "error msg": photoid_errmsg 
                }
                )
                return Response(response_data, content_type="application/json", status=200)
        
        print("enter json")
        request_body = request.get_json()
        args = request_body["args"]
        methods=request_body["method"]
        if 'fetch image' in methods:
            photo_id=args[0]
            success_store, image_binary_error_msg, type_error_no=self.db.fetch_image_data(photo_id)
            if success_store:
                return Response(image_binary_error_msg, mimetype='image/jpeg')
            else:
                    
                response_data = json.dumps(
                {
                    "success": True,
                    "error msg": image_binary_error_msg 
                }
                )
                return Response(response_data, content_type="application/json", status=type_error_no)
        
        return Response('Invalid request method or no file found', status=400)

    def wsgi_app(self, environ, start_response):
        """WSGI application that processes requests and returns responses."""
        request = Request(environ)
        response = self.init_request(request)
        return response(environ, start_response)

    def __call__(self, environ, start_response):
        """The WSGI server calls this method as the WSGI application."""
        return self.wsgi_app(environ, start_response)
    

if __name__ == "__main__":
    from werkzeug.serving import run_simple

    run_simple("192.168.108.1", 80, App())