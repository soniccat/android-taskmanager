package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseWriter implements OutputStreamWriter {
    @Override
    public void beginWrite(OutputStream stream) {
        return stream;
    }

    @Override
    public void write(Object object) throws Exception {
        Course course = (Course)object;

        JsonFactory f = new JsonFactory();
        JsonGenerator g = null;
        try {
            g = f.createGenerator(stream);
            g.writeStartObject();
            g.writeStringField("id", course.getId().toString());
            g.writeStringField("title", course.getTitle());
            g.writeNumberField("createDate", course.getCreateDate().getTime());
            g.writeArrayFieldStart("cards");

            for (Card card : course.getCards()) {
                writeCard(card, g);
            }

            g.writeEndArray();

        } finally {
            if (g != null) {
                try {
                    g.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: separate in different wirter
    private void writeCard(Card card, JsonGenerator g) throws IOException{
        g.writeStartObject();
        g.writeStringField("id",card.getId().toString());
        g.writeStringField("courseId", card.getCourseId().toString());
        g.writeNumberField("createDate", card.getCreateDate().getTime());
        g.writeStringField("term",card.getTerm());
        g.writeStringField("definition",card.getDefinition());

        if (card.getQuizletTerm() != null) {
            g.writeObjectFieldStart("quizletTerm");
            writeQuizletTerm(card.getQuizletTerm(), g);
            g.writeEndObject();
        }

        //TODO: think about storing writers and getting them automatically
        if (card.getProgress() != null) {
            CardProgressWriter progressWriter = new CardProgressWriter();
            g.writeObjectFieldStart("progress");
            progressWriter.write(card.getProgress(), g);
            g.writeEndObject();
        }

        g.writeEndObject();
    }

    // TODO: separate in different writer
    private void writeQuizletTerm(QuizletTerm term, JsonGenerator g) throws IOException {
        g.writeNumberField("id", term.getId());
        g.writeStringField("term", term.getTerm());
        g.writeStringField("definition", term.getDefinition());
        g.writeNumberField("rank", term.getRank());
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
