(function() {
    'use strict';

    angular
        .module('fourthApp')
        .controller('PetDetailController', PetDetailController);

    PetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Pet', 'User'];

    function PetDetailController($scope, $rootScope, $stateParams, previousState, entity, Pet, User) {
        var vm = this;

        vm.pet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('fourthApp:petUpdate', function(event, result) {
            vm.pet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
