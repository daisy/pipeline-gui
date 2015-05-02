pipeline-gui
============

Desktop GUI interface for the pipeline. Written in JavaFX.

# Building this code

Requires Java 8. Get the latest Java 8 for the best accessibility support.

Get the following repositories:
 * pipeline-gui 'javafx' branch
 * pipeline-assembly 'javafx' branch
 * pipeline-framework 'master' branch

Plus any scripts or modules that you want to use.

Build all and run './pipeline2' from the assembly target directory.

# Features
 * View jobs list
 * View job details
 * Access job results in separate application(s)
 * Create new job
 * Run job again

## Keyboard shortcuts
 * New job: Control + N
 * Delete job: Delete
 * Run job (from new job form): Control + R 
 * Run job again: Control + Shift + R
 
# Out of scope for version 1
 * authentication/connecting to a remote pipeline installation
 * batch jobs
 * job template
 * install new scripts from repo
 * autoupdate installed components

## out of scope (maybe forever)
 * user management (e.g. client user account creation)
 * editing content 
 
## proposed behaviors

 * connect directly to pipeline framework (not via webservice)
 * keep jobs in list until user clears them
 * delete a job => default is to keep job output on disk; option to remove data too.
 * list/detail view (jobs list on left in a sidebar)
 * job views for each variation: new job, running job, completed job


