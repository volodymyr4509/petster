(function() {
    'use strict';

    angular
        .module('fourthApp')
        .controller('PetDialogController', PetDialogController);

    PetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Pet', 'User'];

    function PetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Pet, User) {
        var vm = this;

        vm.pet = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.pet.id !== null) {
                Pet.update(vm.pet, onSaveSuccess, onSaveError);
            } else {
                Pet.save(vm.pet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('fourthApp:petUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
