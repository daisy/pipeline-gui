pipeline-gui
============

Desktop GUI interface for the pipeline

# This code

Until the GUI is integrated into the default Pipeline build process, it can be built this way:

Install the correct libraries in Maven:
    cd pipeline-gui/extras
    ./install-to-maven

Build the GUI:
    cd pipeline-gui
    mvn clean install

Then switch to the "gui" branch of the pipeline-assembly project and build the dev launcher version:
    mvn clean package -P dev-launcher

Run this script to copy the correct libraries into the built pipeline-assembly directory:
    cd pipeline-gui/extras
    ./copy-libs

# Project description

##in scope
 * cross or multi platform desktop application
 * start jobs
 * stop jobs
 * view jobs (running/completed/cancelled)
 * view job results in other applications [e.g. browser]


## initially out of scope
 * authentication/connecting to a remote pipeline installation
 * batch jobs
 * duplicate job
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


