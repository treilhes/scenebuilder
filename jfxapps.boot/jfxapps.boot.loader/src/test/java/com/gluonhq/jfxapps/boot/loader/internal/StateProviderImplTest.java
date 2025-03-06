package com.gluonhq.jfxapps.boot.loader.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonhq.jfxapps.boot.api.loader.LoadType;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.registry.model.LayerDefinition;
import com.gluonhq.jfxapps.boot.loader.internal.jpa.model.Extension;
import com.gluonhq.jfxapps.boot.loader.internal.repository.ExtensionRepository;
import com.gluonhq.jfxapps.boot.loader.model.LoadState;

@ExtendWith(MockitoExtension.class)
class StateProviderImplTest {

	private static final UUID ROOT_ID = com.gluonhq.jfxapps.boot.api.loader.extension.Extension.ROOT_ID;
	private static final UUID NO_PARENT = null;
	private static final UUID APP_ID = UUID.randomUUID();

	LoaderMappers mappers = new LoaderMappersImpl();

	@Mock
	RepositoryClient repositoryClient;

	@Mock
	ExtensionRepository repository;

	@Mock
	RegistryManager registryManager;

	@Test
	void null_saved_and_null_registry_states_must_throw_exception() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		assertThrows(IllegalArgumentException.class, () -> {
			stateProvider.applicationState(ROOT_ID, LoadType.LastSuccessfull);
		});
	}

	@Test
	void must_check_merged_items_share_same_id() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(UUID.randomUUID());
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		assertThrows(IllegalArgumentException.class, () -> {
			stateProvider.applicationState(ROOT_ID, LoadType.FullUpdate);
		});
	}

	@Test
	void last_successfull_load_type_must_return_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.LastSuccessfull);

		assertNotNull(state);
		assertEquals(extension, state.getExtension(), "must be the same");
	}

	@Test
	void last_successfull_load_type_mustnt_update_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.LastSuccessfull);

		assertNotNull(state);
		assertEquals(extension, state.getExtension(), "must be the same");
		assertNotEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be different");
	}

	@Test
	void last_successfull_load_type_mustnt_add_extension_to_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		var childLayerDefinition = new LayerDefinition(APP_ID, UUID.randomUUID(), "com.gluonhq", "jfxapps-boot-other", "1.0.0");
		layerDefinition.getChildren().add(childLayerDefinition);
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.LastSuccessfull);

		assertNotNull(state);
		assertEquals(extension, state.getExtension(), "must be the same");
		assertNotEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be different");
		assertTrue(state.getExtensions().isEmpty(), "must be empty");
	}

	@Test
	void update_only_load_type_must_update_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.UpdateOnly);

		assertNotNull(state);
		assertEquals(extension, state.getExtension(), "must be the same");
		assertEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be updated");
	}

	@Test
	void update_only_load_type__mustnt_add_extension_to_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		var childLayerDefinition = new LayerDefinition(APP_ID, UUID.randomUUID(), "com.gluonhq", "jfxapps-boot-other", "1.0.0");
		layerDefinition.getChildren().add(childLayerDefinition);
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));
		Mockito.lenient().when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.UpdateOnly);

		assertNotNull(state);
		assertEquals(extension, state.getExtension(), "must be the same");
		assertEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be different");
		assertTrue(state.getExtensions().isEmpty(), "must be empty");
	}


	@Test
	void full_update_load_type_must_add_extension_to_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");
		var childLayerDefinition = new LayerDefinition(APP_ID, UUID.randomUUID(), "com.gluonhq", "jfxapps-boot-other", "1.0.0");
		layerDefinition.getChildren().add(childLayerDefinition);

		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));
		Mockito.when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.FullUpdate);

		assertNotNull(state);
		assertEquals(extension.getId(), state.getExtension().getId(), "must be the same");
		assertEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be updated");
		assertFalse(state.getExtensions().isEmpty(), "mustn't be empty");
	}

	@Test
	void full_update_load_type_must_delete_extension_from_local_state() {
		var stateProvider = new StateProviderImpl(registryManager, mappers, repositoryClient, repository);

		var extension = new Extension();
		extension.setId(APP_ID);
		var childExtension = new Extension();
		childExtension.setId(UUID.randomUUID());
		childExtension.setParentExtension(extension);
		extension.getExtensions().add(childExtension);

		var layerDefinition = new LayerDefinition(NO_PARENT, APP_ID, "com.gluonhq", "jfxapps-boot-api", "1.0.0");

		Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(extension));
		Mockito.when(registryManager.computeLayerDefinition(Mockito.any())).thenReturn(layerDefinition);

		var state = stateProvider.applicationState(ROOT_ID, LoadType.FullUpdate);

		assertNotNull(state);
		assertEquals(extension.getId(), state.getExtension().getId(), "must be the same");
		assertEquals(layerDefinition.getGroupId(), state.getExtension().getGroupId(), "must be updated");
		assertFalse(state.getExtensions().isEmpty(), "mustn't be empty");
		assertEquals(LoadState.Deleted ,state.getExtensions().iterator().next().getLoadState(),  "must be deleted");
	}
}
