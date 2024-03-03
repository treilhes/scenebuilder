/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package app.root.rest;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.root.model.JfxAppsModel;
import app.root.repository.JfxAppsRepository;
import app.root.service.JfxAppsDataService;
import app.root.test.JfxAppsAspectTest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings("exports")
public class JfxAppsRestController {

    private final JfxAppsRepository repository;
    private final JfxAppsDataService service;
    private final JfxAppsAspectTest jfxAppsAspectTest;

    public JfxAppsRestController(JfxAppsRepository repository, JfxAppsDataService service, JfxAppsAspectTest jfxAppsAspectTest) {
        super();
        this.repository = repository;
        this.service = service;
        this.jfxAppsAspectTest = jfxAppsAspectTest;
    }

    @GetMapping()
    public ResponseEntity<List<JfxAppsModel>> list() {
        var obj = repository.findAll();
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JfxAppsModel> read(@PathVariable(name = "id") Long id) {
        var obj = repository.findById(id).orElse(null);
        return ResponseEntity.ok(obj);
    }

    @PostMapping()
    public ResponseEntity<JfxAppsModel> create(@RequestBody @Valid JfxAppsModel model) {
        var obj = repository.save(model);
        return ResponseEntity.ok(obj);
    }

    @PutMapping()
    public ResponseEntity<JfxAppsModel> update(@RequestBody JfxAppsModel model) {
        var obj = repository.save(model);
        return ResponseEntity.ok(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JfxAppsModel> delete(@PathVariable(name = "id") Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<JfxAppsModel> deleteAll() {
        repository.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/derivation")
    public ResponseEntity<JfxAppsModel> derivation() {
        var obj = repository.getTopByOrderByDataAsc();
        return ResponseEntity.ok(obj);
    }

    @GetMapping("/query")
    public ResponseEntity<List<JfxAppsModel>> query() {
        var obj = repository.customQuery();
        return ResponseEntity.ok(obj);
    }

    @PostMapping("/transaction_rollback_in_service")
    @Transactional
    public ResponseEntity<JfxAppsModel> transaction_rollback_in_service(@RequestBody JfxAppsModel model) {
        service.transactionFailed(model);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transaction_rollback_in_repository")
    @Transactional
    public ResponseEntity<JfxAppsModel> transaction_rollback_in_repository(@RequestBody JfxAppsModel model) throws Exception {
        repository.transactionFailed(model);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/throw_controler_advice_handled_exception")
    public ResponseEntity<?> throwControlerAdviceHandledException() throws Exception {
        throw new Exception("controllerAdviceHandledException");
    }

    @GetMapping("/testing_aspects_are_applied")
    public ResponseEntity<String> testing_aspects_are_applied() throws Exception {
        return ResponseEntity.ok(jfxAppsAspectTest.execute());
    }

    @PostMapping("/testing_validation_is_applied")
    public ResponseEntity<String> testing_validation_is_applied(@RequestBody @Valid JfxAppsModel model) throws Exception {
        return ResponseEntity.ok().build();
    }
}
