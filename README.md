# GVPagerProject
Horizontal auto play GridView

![image](https://github.com/LinhaiGu/GVPagerProject/blob/master/gvp1.gif ) 

![image](https://github.com/LinhaiGu/GVPagerProject/blob/master/gvp2.gif ) 

![image](https://github.com/LinhaiGu/GVPagerProject/blob/master/gvp3.gif ) 

instructions:
----

```
//Set adapter
public void setAdapter(BaseAdapter _adapter)

//refresh
public void notifyDataSetChanged()  

//location
public void setSelection(int position)

//Gets the total number of pages
public int getPageCount() 

//Get the total number
public int getPageSize()

//setting indicator
public void setIndicator(IndicatorView _indicator) 

//Open auto play
public void play() 

//stop auto play
public void stop() 

//Set switch animation
public void setPageTransformer(PageTransformer _pageTransformer)

//Set auto play speed
public void setAutoDuration(int _duration) 
```
