package de.hsrm.orchestrationsystem.testcase_orchestration.reader;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NoArgsConstructor
public class YAMLTestDescriptionReader implements TestDescriptionReader {

    Yaml reader = new Yaml(new Constructor(TestDescription.class));

    @Override
    public TestDescription readTestDescription(Path path) {
        try {
            var stream = new FileInputStream(path.toFile());
            return reader.load(stream);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't read file");
        }
    }
}
