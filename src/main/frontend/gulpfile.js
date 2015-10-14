var gulp = require('gulp'),
    jshint = require('gulp-jshint'),
    uglify = require('gulp-uglify'),
    minifyCSS = require('gulp-minify-css'),
    watch = require('gulp-watch'),
    concat = require('gulp-concat'),
    less = require('gulp-less'),
    clean = require('gulp-clean'),
    connect = require('gulp-connect'),
    sourcemaps = require('gulp-sourcemaps'),
    inject = require('gulp-inject');

application = {
    name: "neo4j-dih-interface",
    target: "../../../target/classes/webapp/",
    less: {
        src: [
            'app/less/application.less'
        ],
        dest: "assets/css/"
    },
    js: {
        src: [
            "app/js/app.js",
            "app/js/error.js",
            "app/js/classes/**.js",
            "app/js/directives/**.js",
            "app/js/services/**.js",
            "app/js/controllers/**.js",
            "app/js/routes.js"
        ],
        deps: [
            "node_modules/spin.js/spin.js",
            "node_modules/codemirror/lib/codemirror.js",
            "node_modules/codemirror/mode/xml/xml.js",
            "node_modules/codemirror/mode/sql/sql.js",
            "node_modules/codemirror/mode/properties/properties.js",
            "node_modules/codemirror/mode/cypher/cypher.js",
            "node_modules/codemirror/mode/javascript/javascript.js",
            "node_modules/angular/angular.js",
            "node_modules/angular-route/angular-route.js",
            "app/patch/ui-codemirror.js"
        ],
        dest: "assets/js"
    },
    assets: {
        src: [
            "app/assets/**/*.*",
            "node_modules/font-awesome/@(fonts)/*.*"
        ],
        dest: "assets"
    }
};

/**
 * LESS compilation.
 */
gulp.task('less', function () {
    var stream = gulp.src(application.less.src)
        .pipe(sourcemaps.init())
        .pipe(less())
        .pipe(minifyCSS())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(application.target + application.less.dest))
        .pipe(connect.reload());
    return stream;
});


/**
 * Concat & minify JS application files.
 */
gulp.task('js', function () {
    var src = application.js.deps.concat(application.js.src)

    var stream = gulp.src(src)
        .pipe(sourcemaps.init())
        .pipe(concat(application.name + '.min.js'))
        .pipe(uglify())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(application.target + application.js.dest))
        .pipe(connect.reload());
    return stream;
});

/**
 * JS Hint task.
 */
gulp.task('jshint', function () {
    var src = application.js.src;
    gulp.src(src)
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

/**
 *  Copy assets to dist directory
 */
gulp.task('assets', function () {
    var stream = gulp.src(application.assets.src)
            .pipe(gulp.dest(application.target + application.assets.dest))
            .pipe(connect.reload());
    return stream;
});

/**
 * Clean task
 */
gulp.task('clean', function () {
    var stream = gulp.src([application.target], {read: false}).pipe(clean());
    return stream;
});

/**
 * Package task.
 */
gulp.task('package', ['less', 'js'], function () {
    // Inject js & css
    var stream = gulp.src('./app/index.html')
        .pipe(inject(gulp.src([application.target + application.js.dest + '**/*.js', application.target + application.less.dest + '**/*.css'], {read: false}), {ignorePath: application.target, addRootSlash: false}))
        .pipe(gulp.dest(application.target))
        .pipe(connect.reload());
    return stream;
});

/**
 * Server task
 */
gulp.task('webserver', function () {
    connect.server({
        port: 8001,
        root: application.target,
        livereload: false
    });
});

/**
 * Gulp watch : on each change file.
 */
gulp.task('watch', function () {
    // JS watch for report
    watch(application.js.src, {read: false}, function () {
        gulp.start("jshint");
    });

    // watching all file for 'rebuild' & reload
    watch("./app/**/*.*",{read: false, verbose:true}, function () {
        gulp.start("build");
    });

});

/**
 * Build the application.
 */
gulp.task('build', ['package', 'assets']);

/**
 * The dev task => run a server with livereload, jshint report, ...
 */
gulp.task('default', ['build', 'webserver', 'watch']);
