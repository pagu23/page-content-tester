package paco.annotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;

public class AnnotationCollector {

    List<Fetch> getAnnotations(Description description) {
        List<Fetch> annotations = new LinkedList<>();

        if (hasMultipleMethodAnnotation(description)) {
            annotations.addAll(Arrays.asList(description.getAnnotation(FetchPages.class).value()));
        } else if (hasSingleMethodAnnotation(description)) {
            annotations.addAll(Collections.singletonList(description.getAnnotation(Fetch.class)));
        } else if (hasMultipleClassAnnotation(description)) {
            annotations.addAll(Arrays.asList(description.getTestClass().getAnnotation(FetchPages.class).value()));
        } else if (hasSingleClassAnnotation(description)) {
            annotations.addAll(Collections.singletonList(description.getTestClass().getAnnotation(Fetch.class)));
        }
        return annotations;
    }

    private boolean hasSingleClassAnnotation(Description description) {
        return description.getTestClass().getAnnotation(Fetch.class) != null;
    }

    private boolean hasMultipleClassAnnotation(Description description) {
        return description.getTestClass().isAnnotationPresent(FetchPages.class);
    }

    private boolean hasSingleMethodAnnotation(Description description) {
        return description.getAnnotation(Fetch.class) != null;
    }

    private boolean hasMultipleMethodAnnotation(Description description) {
        return description.getAnnotation(FetchPages.class) != null;
    }
}