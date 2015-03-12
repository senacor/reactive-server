package com.senacor.reactile.mongo;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public class EmbeddedMongo {

    private final MongodStarter starter = MongodStarter.getInstance(newRuntimeConfig());
    public static final int DEFAULT_MONGO_PORT = 27017;

    private MongodExecutable mongodExe;
    private MongodProcess mongod;
    private Integer port;


    public void start() throws Throwable {
        start(DEFAULT_MONGO_PORT);
    }

    public void start(Integer port) throws Exception {
        this.port = port;
        checkArgument(port != null);
        checkArgument(port > 1);
        if (isStarted()) {
            throw new UnsupportedOperationException("Embedded Mongo already started.");
        }
        if (isRunning()) {
            throw new UnsupportedOperationException("Embedded Mongo is running.");
        }
        cleanArtifactStore();
        mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build());
        System.out.println("Starting Embedded Mongo on port " + port);
        mongod = mongodExe.start();

    }

    public boolean isRunning() {
        return isStarted() && mongod.isProcessRunning();
    }

    private boolean isStarted() {
        return (mongod != null);
    }

    private void cleanArtifactStore() {
        File mongoDirectory = buildDirPath().asFile();
        if (!mongoDirectory.exists()) {
            mongoDirectory.mkdirs();
        } else {
            for (File file : mongoDirectory.listFiles()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Unable to delete Embedded Mongo artifact");
                }
            }
        }
    }

    public void stop() {
        if (mongod == null || mongodExe == null) {
            throw new UnsupportedOperationException("Must start Embedded Mongo before stopping it");
        }
        System.out.println("Stopping Embedded Mongo on port " + port);
        mongodExe.stop();
        mongod.stop();
        cleanArtifactStore();
        mongod = null;
        mongodExe = null;
    }

    private static IRuntimeConfig newRuntimeConfig() {
        Command command = Command.MongoD;
        return new RuntimeConfigBuilder()
                .defaults(command)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(command)
                        .executableNaming((prefix, postfix) -> "embeddedmongo")
                        .tempDir(buildDirPath())
                        .useCache(false))
                .build();
    }

    private static FixedPath buildDirPath() {
        return new FixedPath("build/mongo");
    }
}
