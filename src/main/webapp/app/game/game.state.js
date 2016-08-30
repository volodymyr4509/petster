(function() {
    'use strict';

    angular
        .module('fourthApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('game', {
            parent: 'app',
            url: '/game',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/game/game.html',
                    controller: 'GameController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
