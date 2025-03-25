---
title: "Third-Party Libraries Licenses and Compliance Requirements"
permalink: /devguide/licenses_and_compliance/
excerpt: "Third-Party Libraries Licenses and Compliance Requirements"
toc: true
---

## Third-Party Libraries Licenses and Compliance Requirements

The BSD 3-Clause License (BSD-3) is a permissive open-source license that allows you to use, modify, and distribute code with minimal restrictions. When including third-party libraries in this project without modifying their code, the following licenses are generally compatible:

### ✅ Compatible Licenses for Libraries in a BSD-3 Project
- **BSD (2-Clause, 3-Clause, 4-Clause)**
- **MIT**
- **ISC**
- **Apache 2.0**
- **Zlib/libpng**
- **Boost Software License**
- **Public Domain (CC0, Unlicense, etc.)**
- **MPL 2.0 (Mozilla Public License)**
- **LGPL (Lesser General Public License)**
- **EPL (Eclipse Public License)**

### ❌ Incompatible or Risky Licenses
- **GPL (General Public License, all versions)** → Strong copyleft, requires project to be GPL if used as a library.
- **AGPL (Affero GPL)** → Even stricter than GPL; should be avoided unless explicitly intended.
- **Proprietary Licenses** → Only if explicitly permitted by the license terms.

### 📜 What We Need to Provide for Each License

<table border="1">
  <thead>
    <tr>
      <th width="33%">License</th>
      <th>Requirements for Delivery</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>BSD (2-Clause, 3-Clause, 4-Clause)</strong></td>
      <td>Include the original license file and copyright notice.</td>
    </tr>
    <tr>
      <td><strong>MIT</strong></td>
      <td>Include the license file and copyright notice.</td>
    </tr>
    <tr>
      <td><strong>ISC</strong></td>
      <td>Include the license file and copyright notice.</td>
    </tr>
    <tr>
      <td><strong>Apache 2.0</strong></td>
      <td>Include the license file, copyright notice, and a NOTICE file (if provided). If you modify the library, you must mark changes.</td>
    </tr>
    <tr>
      <td><strong>Zlib/libpng</strong></td>
      <td>Include the license file.</td>
    </tr>
    <tr>
      <td><strong>Boost Software License</strong></td>
      <td>Include the license file.</td>
    </tr>
    <tr>
      <td><strong>Public Domain (CC0, Unlicense, etc.)</strong></td>
      <td>No legal requirements, but attribution is good practice.</td>
    </tr>
    <tr>
      <td><strong>MPL 2.0</strong></td>
      <td>Include the license file. If modified, provide source code for changes.</td>
    </tr>
    <tr>
      <td><strong>LGPL (Lesser GPL)</strong></td>
      <td>Include the license file and inform users they can relink with a modified version of the library. You must provide a way to replace the LGPL library.</td>
    </tr>
    <tr>
      <td><strong>EPL (Eclipse Public License)</strong></td>
      <td>Include the license file and, if modified, provide source code for changes.</td>
    </tr>
  </tbody>
</table>

If the project redistributes compiled binaries, make sure to provide the required license files inside the distribution package.

