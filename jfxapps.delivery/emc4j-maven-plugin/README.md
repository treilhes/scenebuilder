
available installation path:

- app A from scratch
	* install customized booter A version X.X.X on disk (user choice)
	* on launch uploads to user home .emc4j the core C version X.X.X the app A version A.A.A
	
- app B from scratch
  * install customized booter B version Y.Y.Y on disk (user choice)
	* on launch uploads to user home .emc4j the core C version X.X.X the app B version Y.Y.Y
	
- app A to A2 upgrade (Not needed, app upgrade handled by system)
  * install customized booter A version X.X.X + 1 on disk (user choice)
  
- app B to B2 upgrade (Not needed, app upgrade handled by system)
  * install customized booter B version Y.Y.Y + 1 on disk (user choice)
  
- app A uninstall
  * pre uninstall : run app with uuid and uninstall option to cleanup user home .emc4j
  * standard uninstall of booter A from disk
  
- app B uninstall
  * pre uninstall : run app with uuid and uninstall option to cleanup user home .emc4j
  * standard uninstall of booter B from disk

  
  HOW TO HANDLE DIFFERENT BOOTER VERSIONS ?
  - each booter version is independent, so if user install booter A version X.X.X 
    and later install booter A version X.X.X + 1, both can coexist on disk
    
    BUT WHAT ARE THE IMPLICATIONS ?
    
    * booter versions can use different java versions
    > need a new process to launch the app with the right java version
    
  HOW TO HANDLE DIFFERENT CORE VERSIONS ?
  - there is only one core version on user home .emc4j but each booter can install its own core version
    so if booter A install core version C version X.X.X and later booter B install core version C version X.X.X + 1
    both core versions will coexist on user home .emc4j
    
    BUT WHAT ARE THE IMPLICATIONS ?
    
    * core versions can use different javafx versions
    > need a new process to launch the app with the right javafx version