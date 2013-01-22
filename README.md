CustomTaskAPI - v1.0
==========

Customized Async Task API which returns response from requested URL. 
You can call GET or POST service with only 2 lines of code.
Then get response as a string in a Message Object.
ProgressBar was already integrated. 
HTTP Connection was built under AsyncTask so you do not need to re-write each-time .

to start it you may download jar from

<a href="http://metacons.com/customtaskapi_v1.0.jar">JAR V1.0</a>

Then you check-out below source code or Example <a href="https://github.com/metacons/customtask/blob/master/CustomTaskAPI/src/com/metacons/customtaskapi/MainActivity.java">MainActivity</a>



	
    /** create task variable as a global **/
    CustomTask task = null;
		/**
		 * init task variable with context and callback listener
		 */
		task = new CustomTask(this, taskFinished);

		/**
		 * If you call a POST service then call with 2 parameters
		 * 
		 * @param1 = URL address
		 * @param2 = Post String
		 */
		// task.execute("http:/...","<?xml version=\"1.0\"... ");

		/**
		 * If you call a GET service then call with 1 parameter
		 * 
		 * @param = URL GET address
		 */

		task.execute("http://erayince.com.tr/egitimws/service1.json");
		/** THATS ALL **/

		/*
		 * OPTIONAL PARTS
		 */

		/*
		 * If do not display progress then create task with overloaded
		 * constructor
		 */
		// task = new CustomTask(this, taskFinished, false);

	}
