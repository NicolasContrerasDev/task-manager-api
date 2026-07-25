package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.models.Task;
import com.portfolio.tareas.tareas_api.models.Workspace;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;

@Service
public class TaskExportService {

	public byte[] exportAssignedTasks(Workspace workspace, AppUser user, List<Task> tasks) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			 ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
			addEntry(zip, "[Content_Types].xml", contentTypesXml());
			addEntry(zip, "_rels/.rels", rootRelationshipsXml());
			addEntry(zip, "xl/workbook.xml", workbookXml());
			addEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelationshipsXml());
			addEntry(zip, "xl/worksheets/sheet1.xml", sheetXml(workspace, user, tasks));
			zip.finish();
			return outputStream.toByteArray();
		} catch (IOException ex) {
			throw new IllegalStateException("No se pudo generar el archivo Excel", ex);
		}
	}

	private void addEntry(ZipOutputStream zip, String path, String content) throws IOException {
		zip.putNextEntry(new ZipEntry(path));
		zip.write(content.getBytes(StandardCharsets.UTF_8));
		zip.closeEntry();
	}

	private String contentTypesXml() {
		return """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
				<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
				<Default Extension="xml" ContentType="application/xml"/>
				<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
				<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
			</Types>
			""";
	}

	private String rootRelationshipsXml() {
		return """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
				<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
			</Relationships>
			""";
	}

	private String workbookXml() {
		return """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
				<sheets>
					<sheet name="Mis tareas" sheetId="1" r:id="rId1"/>
				</sheets>
			</workbook>
			""";
	}

	private String workbookRelationshipsXml() {
		return """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
				<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
			</Relationships>
			""";
	}

	private String sheetXml(Workspace workspace, AppUser user, List<Task> tasks) {
		StringBuilder rows = new StringBuilder();
		rows.append(row(1, "Area de trabajo", workspace.getName()));
		rows.append(row(2, "Usuario", user.getUsername()));
		rows.append(row(4, "ID", "Titulo", "Descripcion", "Estado", "Asignado a", "Creada"));

		int rowNumber = 5;
		for (Task task : tasks) {
			rows.append(row(
				rowNumber++,
				String.valueOf(task.getId()),
				task.getTitle(),
				task.getDescription() != null ? task.getDescription() : "",
				task.getStatus().name(),
				task.getAssignedTo().getUsername(),
				task.getCreatedAt() != null ? task.getCreatedAt().toString() : ""
			));
		}

		return """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
				<cols>
					<col min="1" max="1" width="14" customWidth="1"/>
					<col min="2" max="2" width="28" customWidth="1"/>
					<col min="3" max="3" width="42" customWidth="1"/>
					<col min="4" max="4" width="18" customWidth="1"/>
					<col min="5" max="5" width="22" customWidth="1"/>
					<col min="6" max="6" width="24" customWidth="1"/>
				</cols>
				<sheetData>
			""" + rows + """
				</sheetData>
			</worksheet>
			""";
	}

	private String row(int rowNumber, String... values) {
		StringBuilder cells = new StringBuilder();
		for (int index = 0; index < values.length; index++) {
			cells.append(cell(index, rowNumber, values[index]));
		}
		return "<row r=\"" + rowNumber + "\">" + cells + "</row>";
	}

	private String cell(int columnIndex, int rowNumber, String value) {
		String reference = columnName(columnIndex) + rowNumber;
		return "<c r=\"" + reference + "\" t=\"inlineStr\"><is><t>" + escapeXml(value) + "</t></is></c>";
	}

	private String columnName(int columnIndex) {
		StringBuilder name = new StringBuilder();
		int index = columnIndex;
		do {
			name.insert(0, (char) ('A' + (index % 26)));
			index = (index / 26) - 1;
		} while (index >= 0);
		return name.toString();
	}

	private String escapeXml(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&apos;");
	}
}
