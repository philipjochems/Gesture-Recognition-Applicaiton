import os
from werkzeug.utils import secure_filename
from flask import request
from flask import Flask

 
# Flask Constructor
app = Flask(__name__)
 
# decorator to associate
# a function with the url
@app.route("/")
def showHomePage():
      # response from the server
    print('connected')
    return "Connected"

@app.route("/debug", methods=["POST"])
def debug():
    myFile = request.files["file"]
    count=0
    arr = os.listdir()
    for i in arr:
        if myFile.filename in i:
            count+=1
    myFile.save(secure_filename(myFile.filename+str(count)+'_Jochems.mp4'))
    print(secure_filename(myFile.filename+str(count)+'_Jochems.mp4')+' created')
    return "received"
 
if __name__ == "__main__":
  app.run(host="0.0.0.0")
