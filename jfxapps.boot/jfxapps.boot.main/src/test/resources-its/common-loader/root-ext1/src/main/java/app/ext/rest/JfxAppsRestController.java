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
package app.ext.rest;

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

import app.ext.model.JfxAppsModel;
import app.ext.repository.JfxAppsRepository;
import app.ext.service.JfxAppsDataService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings("exports")
public class JfxAppsRestController {

    private final JfxAppsRepository repository;
    private final JfxAppsDataService service;

    public JfxAppsRestController(JfxAppsRepository repository, JfxAppsDataService service) {
        super();
        this.repository = repository;
        this.service = service;
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

    @GetMapping("/transaction_rollback_in_service")
    @Transactional
    public ResponseEntity<JfxAppsModel> transaction_rollback_in_service() {
        service.transactionFailed();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transaction_rollback_in_repository")
    @Transactional
    public ResponseEntity<JfxAppsModel> transaction_rollback_in_repository() {
        JfxAppsModel rm = new JfxAppsModel();
        rm.setData("MUST NOT BE INSERTED DUE TO ROLLBACK");
        JfxAppsModel rm2 = new JfxAppsModel();
        rm2.setData("NOTBAD");
        rm2.setOther(null);
        JfxAppsModel rm3 = new JfxAppsModel();
        rm3.setData("BAD");
        rm3.setOther(null);

        repository.saveAll(List.of(rm,rm2));//,rm3));
        return ResponseEntity.ok().build();
    }
}
