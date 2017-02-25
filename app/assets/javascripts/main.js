
(function(requirejs) {
    'use strict';

    // -- RequireJS config --
    requirejs.config({
        waitSeconds: 0, // we need to wait for all libraries to be loaded... so disable the default timeout
        // jquery needs to be first no matter what!
        deps: ['jquery'],
        // Packages = top-level folders; loads a contained file named 'main.js"
        packages: ['controllers', 'controllers/catalog', 'controllers/finance',
        	       {  name: 'moment', location: '../lib/momentjs', main: 'moment' }],
        shim: {
            'jsRoutes': {
                deps: [],
                //  it's not a RequireJS module, so we have to tell it what var is returned
                exports: 'jsRoutes'
            },
            'modernizr': { deps: [], exports: 'Modernizr' },
//            'moment-pt-br': ['moment'],
            'bootstrap': ['jquery'],
            'angular': {
                deps: ['jquery'],
                exports: 'angular'
            },
            'angular-pt-br': ['angular'],
            'angular-route': ['angular'],
            'angular-resource': ['angular'],
            'angular-messages': ['angular'],
            'angular-animate': ['angular'],
            'angular-aria': ['angular'],
            'angular-sanitize': ['angular'],
            'angular-ui-router': ['angular'],
            'angular-ui-bootstrap': ['angular'],
            'angular-toastr': ['angular'],
            'metis-menu': ['bootstrap'],
            'ngTable': ['angular']
        },
        paths: {
            'requirejs': ['../lib/requirejs/require'],
            'modernizr': ['../lib/modernizr/modernizr'],
            'jquery': ['../lib/jquery/jquery'],
            'bootstrap': ['../lib/bootstrap/js/bootstrap'],
            'angular': ['../lib/angularjs/angular'],
            'angular-pt-br': ['../lib/angularjs/i18n/angular-locale_pt-br'],
            'angular-route': ['../lib/angularjs/angular-route'],
            'angular-resource': ['../lib/angularjs/angular-resource'],
            'angular-messages': ['../lib/angularjs/angular-messages'],
            'angular-animate': ['../lib/angularjs/angular-animate'],
            'angular-aria': ['../lib/angularjs/angular-aria'],
            'angular-sanitize': ['../lib/angularjs/angular-sanitize'],
            'angular-ui-router': ['../lib/angular-ui-router/release/angular-ui-router'],
            'domReady': ['../lib/requirejs-domready/domReady'],
            'angular-ui-bootstrap': ['../lib/angular-bootstrap/ui-bootstrap-tpls'],
            'angular-toastr': ['../lib/angular-toastr/dist/angular-toastr.tpls'],
            'metis-menu': ['../lib/metisMenu/metisMenu'],
            'jsRoutes': ['/jsroutes'],
            'ngTable': ['../lib/ng-table/dist/ng-table']
        }
    });

    requirejs.onError = function(err) {
        console.log(err);
        console.log(err.requireType);
        console.log('modules: ' + err.requireModules);
        throw err;
    };
//
//    require([/*'modernizr'*/, 'moment', 'bootstrap', 'angular-messages',/*'angular-animate',*/ 'angular-aria', 'angular-sanitize'], 
//        function(/*Modernizr, */moment) {
//            //Modernizr.addTest('filereader', !!(window.File && window.FileList && window.FileReader));
//            //window.moment = moment;
//            //moment.locale('pt-BR');
//        }
//    );
    
    require(['moment', 'moment/locale/pt-br'], function(moment) {
        window.moment = moment;
        moment.locale('pt-BR');
    });
    
    require(['metis-menu'], function(moment) {
        $('#side-menu').metisMenu();
    });

    require(['angular', 'moment', 'angular-animate', 'angular-pt-br', 'angular-aria', 'angular-sanitize', 'metis-menu',
             'angular-ui-bootstrap', 'bootstrap', 'angular-toastr', './app'],
         function(angular) {
            require(['domReady'], function(document) {
                angular.bootstrap(document, ['app'], { strictDi: true });
            });
        }
    );
})(requirejs);
